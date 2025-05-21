package com.martinez.victor.tfg_owner_app.utilities;

import static android.telephony.TelephonyManager.SIM_STATE_CARD_IO_ERROR;
import static android.telephony.TelephonyManager.SIM_STATE_UNKNOWN;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.martinez.victor.tfg_owner_app.DeviceOwnerReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DeviceInfo {
    private final static String tag = "DeviceInfo";
    private final DevicePolicyManager dpm;
    private final TelephonyManager telephonyManager;
    private final SubscriptionManager subscriptionManager;

    public DeviceInfo(Context context) {
        DeviceOwner.grantAllPermissions(context);
        dpm = DeviceOwner.getDPM(context);
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            subscriptionManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        } else {
            subscriptionManager = null;
        }
    }

    private String getDPMEnrollmentID() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return dpm.getEnrollmentSpecificId();
        }
        return null;
    }

    private static class SlotInfo implements IToJson {
        public String id;
        public String subscriberID;
        public String simSerial;
        public String networkOperator;
        public String networkOperatorName;
        public int simState;
        public int phoneType;
        public String imei;
        public String meid;

        public JSONObjectCustom toJson() {
            JSONObjectCustom obj = new JSONObjectCustom();
            try {
                obj.put("id", id);
                obj.put("subscriberID", subscriberID);
                obj.put("simSerial", simSerial);
                obj.put("networkOperator", networkOperator);
                obj.put("networkOperatorName", networkOperatorName);
                obj.put("simState", simState);
                obj.put("phoneType", phoneType);
                obj.put("imei", imei);
                obj.put("meid", meid);
            } catch (JSONException e) {
                Log.e(tag, "Could not convert slot info to JSON: " + e.getMessage());
            }
            return obj;
        }
    }

    @SuppressLint("HardwareIds")
    private ArrayList<SlotInfo> getSlotsInfo() {
        ArrayList<SlotInfo> slotsInfo = new ArrayList<>();
        try {
            // Android <=6 and below does not support reading per slot information
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                SlotInfo slotInfo = new SlotInfo();
                slotInfo.phoneType = telephonyManager.getPhoneType();
                slotInfo.id = telephonyManager.getDeviceId();
                slotInfo.subscriberID = telephonyManager.getSubscriberId();
                slotInfo.simSerial = telephonyManager.getSimSerialNumber();
                slotInfo.networkOperator = telephonyManager.getNetworkOperator();
                slotInfo.networkOperatorName = telephonyManager.getNetworkOperatorName();
                slotInfo.simState = telephonyManager.getSimState();
                slotsInfo.add(slotInfo);
                return slotsInfo;
            }

            int slotCount = 0;
            // Uses more modern API call for Android >=11
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                slotCount = telephonyManager.getActiveModemCount();
            } else {
                slotCount = telephonyManager.getPhoneCount();
            }

            List<SubscriptionInfo> subscriptionsInfo = subscriptionManager.getActiveSubscriptionInfoList();

            if (subscriptionsInfo == null || subscriptionsInfo.isEmpty()) {
                return slotsInfo;
            }

            for (SubscriptionInfo subscriptionInfo : subscriptionsInfo) {
                SlotInfo slotInfo = new SlotInfo();
                int subscriptionId = subscriptionInfo.getSubscriptionId();
                TelephonyManager tmForSub = telephonyManager.createForSubscriptionId(subscriptionId);
                slotInfo.id = String.valueOf(subscriptionId);
                slotInfo.subscriberID = tmForSub.getSubscriberId();
                slotInfo.simSerial = tmForSub.getSimSerialNumber();
                slotInfo.networkOperator = tmForSub.getNetworkOperator();
                slotInfo.networkOperatorName = tmForSub.getNetworkOperatorName();
                slotInfo.simState = tmForSub.getSimState();
                slotInfo.phoneType = tmForSub.getPhoneType();

                int slotIndex = subscriptionInfo.getSimSlotIndex();

                // Android <8 does not support separate apis for meid and imei
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    if (slotInfo.phoneType == TelephonyManager.PHONE_TYPE_GSM) {
                        slotInfo.imei = telephonyManager.getDeviceId(slotIndex);
                    } else if (slotInfo.phoneType == TelephonyManager.PHONE_TYPE_CDMA) {
                        slotInfo.meid = telephonyManager.getDeviceId(slotIndex);
                    }
                }

                // Android >=8 supports separate imei and meid apis
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                    if (slotInfo.phoneType == TelephonyManager.PHONE_TYPE_GSM) {
                        slotInfo.imei = telephonyManager.getImei(slotIndex);
                    } else if (slotInfo.phoneType == TelephonyManager.PHONE_TYPE_CDMA) {
                        slotInfo.meid = telephonyManager.getMeid(slotIndex);
                    }
                }

                slotsInfo.add(slotInfo);
            }

        } catch (SecurityException e) {
            Log.d(tag, "Could not get slot information for device. SecurityException: " + e.getMessage());
        }
        return slotsInfo;
    }

    public JSONObjectCustom toJson() {
        JSONObjectCustom obj = new JSONObjectCustom();
        try {
            obj.put("dpm_enrollment_id", getDPMEnrollmentID());
            obj.put("build", DeviceBuildInfo.toJSON());
            obj.put("slots", getSlotsInfo());
        } catch (JSONException e) {
            Log.e(tag, "Could not device info to JSON: " + e.getMessage());
        }
        return obj;
    }

}
