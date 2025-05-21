package com.martinez.victor.tfg_owner_app.utilities;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class DeviceBuildInfo {
    private static final String tag = "DeviceBuildInfo";

    public static String getBoard() {
        return Build.BOARD.equals(Build.UNKNOWN) ? null : Build.BOARD;
    }

    public static String getBootloader() {
        return Build.BOOTLOADER.equals(Build.UNKNOWN) ? null : Build.BOOTLOADER;
    }

    public static String getBrand() {
        return Build.BRAND.equals(Build.UNKNOWN) ? null : Build.BRAND;
    }

    public static String getDevice() {
        return Build.DEVICE.equals(Build.UNKNOWN) ? null : Build.DEVICE;
    }

    public static String getAndroidBuildDisplay() {
        return Build.DISPLAY.equals(Build.UNKNOWN) ? null : Build.DISPLAY;
    }

    public static String getAndroidBuildFingerprint() {
        return Build.FINGERPRINT.equals(Build.UNKNOWN) ? null : Build.FINGERPRINT;
    }

    public static String getHardwareName() {
        return Build.HARDWARE.equals(Build.UNKNOWN) ? null : Build.HARDWARE;
    }

    public static String getHost() {
        return Build.HOST.equals(Build.UNKNOWN) ? null : Build.HOST;
    }

    public static String getID() {
        return Build.ID.equals(Build.UNKNOWN) ? null : Build.ID;
    }

    public static String getManufacturer() {
        return Build.MANUFACTURER.equals(Build.UNKNOWN) ? null : Build.MANUFACTURER;
    }

    public static String getModel() {
        return Build.MODEL.equals(Build.UNKNOWN) ? null : Build.MODEL;
    }

    public static String getODMSKU() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return Build.ODM_SKU.equals(Build.UNKNOWN) ? null : Build.ODM_SKU;
        }
        return null;
    }

    public static String getProductName() {
        return Build.PRODUCT.equals(Build.UNKNOWN) ? null : Build.PRODUCT;
    }

    @SuppressLint("HardwareIds")
    public static String getSerial() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return Build.SERIAL.equals(Build.UNKNOWN) ? null : Build.SERIAL;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return Build.getSerial().equals(Build.UNKNOWN) ? null : Build.getSerial();
        }
        return null;
    }

    public static String getSKU() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return Build.SKU.equals(Build.UNKNOWN) ? null : Build.SKU;
        }
        return null;
    }

    public static String getSOCManufacturer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return Build.SOC_MANUFACTURER.equals(Build.UNKNOWN) ? null : Build.SOC_MANUFACTURER;
        }
        return null;
    }

    public static String getSOCModel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return Build.SOC_MODEL.equals(Build.UNKNOWN) ? null : Build.SOC_MODEL;
        }
        return null;
    }

    public static String[] getSupported32bitABIs() {
        return Build.SUPPORTED_32_BIT_ABIS;
    }

    public static String[] getSupported64bitABIs() {
        return Build.SUPPORTED_64_BIT_ABIS;
    }

    public static String[] getSupportedABIs() {
        return Build.SUPPORTED_ABIS;
    }

    public static String getAndroidBuildTags() {
        return Build.TAGS.equals(Build.UNKNOWN) ? null : Build.TAGS;
    }

    public static long getAndroidBuildTimestampMs() {
        return Build.TIME;
    }

    public static String getAndroidBuildType() {
        return Build.TYPE.equals(Build.UNKNOWN) ? null : Build.TYPE;
    }

    public static String getAndroidBuildUser() {
        return Build.USER.equals(Build.UNKNOWN) ? null : Build.USER;
    }

    public static int getAndroidBuildSdkVer() {
        return Build.VERSION.SDK_INT;
    }

    private static JSONObject toJSONAndroidBuild() throws JSONException {
        JSONObjectCustom androidBuild = new JSONObjectCustom();
        androidBuild.put("sdk", getAndroidBuildSdkVer());
        androidBuild.put("display", getAndroidBuildDisplay());
        androidBuild.put("fingerprint", getAndroidBuildFingerprint());
        androidBuild.put("timestamp_ms", getAndroidBuildTimestampMs());
        androidBuild.put("type", getAndroidBuildType());
        androidBuild.put("user", getAndroidBuildUser());
        androidBuild.put("tags", getAndroidBuildTags());
        return androidBuild;
    }

    public static JSONObjectCustom toJSON() {
        try {
            JSONObjectCustom json = new JSONObjectCustom();
            json.put("board_name", getBoard());
            json.put("bootloader", getBootloader());
            json.put("brand", getBrand());
            json.put("device", getDevice());
            json.put("android_build_info", toJSONAndroidBuild());
            json.put("hardware_name", getHardwareName());
            json.put("host", getHost());
            json.put("id", getID());
            json.put("manufacturer", getManufacturer());
            json.put("model", getModel());
            json.put("odm_sku", getODMSKU());
            json.put("product_name", getProductName());
            json.put("serial", getSerial());
            json.put("sku", getSKU());
            json.put("soc_manufacturer", getSOCManufacturer());
            json.put("soc_model", getSOCModel());
            json.put("supported_32_bit_abis", getSupported32bitABIs());
            json.put("supported_64_bit_abis", getSupported64bitABIs());
            json.put("supported_abis", getSupportedABIs());
            return json;
        } catch (JSONException e) {
            Log.e(tag, "Could not convert device build info to JSON: " + e.getMessage());
        }
        return new JSONObjectCustom();
    }
}
