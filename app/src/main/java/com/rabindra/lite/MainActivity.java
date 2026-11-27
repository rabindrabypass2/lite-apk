package com.rabindra.lite;



import android.annotation.SuppressLint;

import android.content.Context;

import android.content.Intent;

import android.graphics.Bitmap;

import android.graphics.Color;

import android.net.http.SslError;

import android.os.Bundle;

import android.os.Handler;

import android.os.Looper;

import android.os.PowerManager;

import android.view.View;

import android.webkit.CookieManager;

import android.webkit.JavascriptInterface;

import android.webkit.JsPromptResult;

import android.webkit.JsResult;

import android.webkit.SslErrorHandler;

import android.webkit.WebChromeClient;

import android.webkit.WebResourceError;

import android.webkit.WebResourceRequest;

import android.webkit.WebSettings;

import android.webkit.WebStorage;

import android.webkit.WebView;

import android.webkit.WebViewClient;

import android.widget.Button;

import android.widget.ProgressBar;

import android.widget.TextView;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import androidx.webkit.WebSettingsCompat;

import androidx.webkit.WebViewFeature;

import java.util.Collections;



import java.io.BufferedReader;

import java.io.DataOutputStream;

import java.io.InputStreamReader;

import java.net.HttpURLConnection;

import java.net.URL;

import java.util.Random;



public class MainActivity extends AppCompatActivity {



    private WebView webView;

    private Button btnAirplane;

    private Button btnReload;

    private TextView tvStatus;

    private TextView tvIp;

    private ProgressBar progressBar;

    private PowerManager.WakeLock wakeLock;



    private static final String TARGET_URL = "https://liteshort.com/TnpiIDW4";

    private static final Random RANDOM = new Random();



    // Paired Per-Cycle Device Profiles (UA + CPU + RAM + GPU) so every IP rotation gets a unique browser fingerprint
    private static class DeviceProfile {
        final String ua;
        final int cores;
        final int memGb;
        final String gpuVendor;
        final String gpuRenderer;
        DeviceProfile(String ua, int cores, int memGb, String gpuVendor, String gpuRenderer) {
            this.ua = ua;
            this.cores = cores;
            this.memGb = memGb;
            this.gpuVendor = gpuVendor;
            this.gpuRenderer = gpuRenderer;
        }
    }

    private static final DeviceProfile[] DEVICE_PROFILES = new DeviceProfile[]{
        // Flagship & Upper Mid-Range
        new DeviceProfile("Mozilla/5.0 (Linux; Android 14; Pixel 8 Pro) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.6778.135 Mobile Safari/537.36", 9, 12, "ARM", "Mali-G715"),
        new DeviceProfile("Mozilla/5.0 (Linux; Android 14; SM-S928B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.6778.135 Mobile Safari/537.36", 8, 12, "Qualcomm", "Adreno (TM) 750"),
        new DeviceProfile("Mozilla/5.0 (Linux; Android 14; CPH2581) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.6778.86 Mobile Safari/537.36", 8, 16, "Qualcomm", "Adreno (TM) 750"),
        new DeviceProfile("Mozilla/5.0 (Linux; Android 14; Pixel 7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.6778.86 Mobile Safari/537.36", 8, 8, "ARM", "Mali-G710 MC10"),
        new DeviceProfile("Mozilla/5.0 (Linux; Android 13; SM-G998B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.6723.102 Mobile Safari/537.36", 8, 12, "ARM", "Mali-G78 MP14"),
        new DeviceProfile("Mozilla/5.0 (Linux; Android 14; NE2215) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.6778.135 Mobile Safari/537.36", 8, 8, "Qualcomm", "Adreno (TM) 730"),
        new DeviceProfile("Mozilla/5.0 (Linux; Android 13; 2211133G) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.6723.102 Mobile Safari/537.36", 8, 8, "Qualcomm", "Adreno (TM) 740"),
        new DeviceProfile("Mozilla/5.0 (Linux; Android 14; CPH2449) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.6778.135 Mobile Safari/537.36", 8, 16, "Qualcomm", "Adreno (TM) 740"),
        // Samsung A / M / F Series
        new DeviceProfile("Mozilla/5.0 (Linux; Android 14; SM-A556B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.6723.102 Mobile Safari/537.36", 8, 8, "Samsung", "Samsung Xclipse 530"),
        new DeviceProfile("Mozilla/5.0 (Linux; Android 14; SM-A546E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.6778.86 Mobile Safari/537.36", 8, 8, "ARM", "Mali-G68 MP5"),
        new DeviceProfile("Mozilla/5.0 (Linux; Android 14; SM-M346B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.6723.86 Mobile Safari/537.36", 8, 6, "ARM", "Mali-G68 MP4"),
        new DeviceProfile("Mozilla/5.0 (Linux; Android 13; SM-M146B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.6668.100 Mobile Safari/537.36", 8, 6, "ARM", "Mali-G68 MP2"),
        new DeviceProfile("Mozilla/5.0 (Linux; Android 14; SM-E546B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.6723.102 Mobile Safari/537.36", 8, 8, "ARM", "Mali-G68 MP5"),
        new DeviceProfile("Mozilla/5.0 (Linux; Android 14; SM-A156E) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.6778.86 Mobile Safari/537.36", 8, 6, "ARM", "Mali-G57 MC2"),
        // Redmi / Xiaomi / POCO
        new DeviceProfile("Mozilla/5.0 (Linux; Android 14; 23090RA98G) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.6778.135 Mobile Safari/537.36", 8, 8, "ARM", "Mali-G615 MC6"),
        new DeviceProfile("Mozilla/5.0 (Linux; Android 14; 2311DRK48I) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.6778.135 Mobile Safari/537.36", 8, 12, "ARM", "Mali-G615 MC6"),
        new DeviceProfile("Mozilla/5.0 (Linux; Android 14; 23049PCD8I) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.6723.102 Mobile Safari/537.36", 8, 8, "Qualcomm", "Adreno (TM) 725"),
        new DeviceProfile("Mozilla/5.0 (Linux; Android 13; 2201116PI) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.6668.100 Mobile Safari/537.36", 8, 6, "Qualcomm", "Adreno (TM) 619"),
        new DeviceProfile("Mozilla/5.0 (Linux; Android 14; 23076RN4BI) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.6723.86 Mobile Safari/537.36", 8, 6, "Qualcomm", "Adreno (TM) 613"),
        // Vivo / iQOO
        new DeviceProfile("Mozilla/5.0 (Linux; Android 14; V2319) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.6778.135 Mobile Safari/537.36", 8, 8, "Qualcomm", "Adreno (TM) 740"),
        new DeviceProfile("Mozilla/5.0 (Linux; Android 14; I2219) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.6778.86 Mobile Safari/537.36", 8, 8, "Qualcomm", "Adreno (TM) 644"),
        new DeviceProfile("Mozilla/5.0 (Linux; Android 14; V2250) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.6723.102 Mobile Safari/537.36", 8, 8, "ARM", "Mali-G610 MC6"),
        // Realme / OnePlus Nord / OPPO
        new DeviceProfile("Mozilla/5.0 (Linux; Android 14; RMX3840) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.6723.86 Mobile Safari/537.36", 8, 12, "Qualcomm", "Adreno (TM) 735"),
        new DeviceProfile("Mozilla/5.0 (Linux; Android 14; RMX3771) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.6723.102 Mobile Safari/537.36", 8, 8, "ARM", "Mali-G68 MC4"),
        new DeviceProfile("Mozilla/5.0 (Linux; Android 14; CPH2613) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.6778.135 Mobile Safari/537.36", 8, 8, "Qualcomm", "Adreno (TM) 732"),
        new DeviceProfile("Mozilla/5.0 (Linux; Android 13; CPH2493) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.6668.100 Mobile Safari/537.36", 8, 8, "Qualcomm", "Adreno (TM) 642L"),
        // Nothing / Motorola / Infinix / Tecno
        new DeviceProfile("Mozilla/5.0 (Linux; Android 14; A065) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.6778.135 Mobile Safari/537.36", 8, 12, "Qualcomm", "Adreno (TM) 730"),
        new DeviceProfile("Mozilla/5.0 (Linux; Android 14; motorola edge 50 pro) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.6778.135 Mobile Safari/537.36", 8, 12, "Qualcomm", "Adreno (TM) 720"),
        new DeviceProfile("Mozilla/5.0 (Linux; Android 14; Infinix X6850) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.6723.86 Mobile Safari/537.36", 8, 8, "ARM", "Mali-G57 MC2"),
        new DeviceProfile("Mozilla/5.0 (Linux; Android 13; TECNO CK7n) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.6668.100 Mobile Safari/537.36", 8, 8, "ARM", "Mali-G57 MC2")
    };

    private int lastProfileIdx = -1;
    private volatile DeviceProfile activeProfile = DEVICE_PROFILES[0];
    private volatile String currentSpoofJs = "";

    private void rotateDeviceProfile() {
        int nextIdx = RANDOM.nextInt(DEVICE_PROFILES.length);
        if (DEVICE_PROFILES.length > 1 && nextIdx == lastProfileIdx) {
            nextIdx = (nextIdx + 1 + RANDOM.nextInt(DEVICE_PROFILES.length - 1)) % DEVICE_PROFILES.length;
        }
        lastProfileIdx = nextIdx;
        DeviceProfile p = DEVICE_PROFILES[nextIdx];
        activeProfile = p;
        int canvasXor = 1 + RANDOM.nextInt(250);
        int canvasCh = RANDOM.nextInt(3);
        double audioDelta = (1 + RANDOM.nextInt(95)) * 1e-7;
        int touchPts = (RANDOM.nextBoolean()) ? 5 : 10;
        String safeVendor = p.gpuVendor.replace("'", "");
        String safeRenderer = p.gpuRenderer.replace("'", "");

        currentSpoofJs =
            "(function(){" +
            "try{" +
            "if(window.__fp_applied)return;" +
            "window.__fp_applied=true;" +
            "Object.defineProperty(navigator,'webdriver',{get:function(){return false;},configurable:true});" +
            "Object.defineProperty(navigator,'hardwareConcurrency',{get:function(){return " + p.cores + ";},configurable:true});" +
            "Object.defineProperty(navigator,'deviceMemory',{get:function(){return " + p.memGb + ";},configurable:true});" +
            "Object.defineProperty(navigator,'maxTouchPoints',{get:function(){return " + touchPts + ";},configurable:true});" +
            "if(!window.chrome){" +
            "window.chrome={" +
            "runtime:{id:undefined,connect:function(){},sendMessage:function(){},onMessage:{addListener:function(){}}}," +
            "loadTimes:function(){return{requestTime:0,startLoadTime:0,commitLoadTime:0,finishDocumentLoadTime:0,finishLoadTime:0,firstPaintTime:0,navigationType:'Other',wasFetchedViaSpdy:false,wasNpnNegotiated:false,npnNegotiatedProtocol:'',wasAlternateProtocolAvailable:false,connectionInfo:'http/1.1'};}," +
            "csi:function(){return{startE:Date.now(),onloadT:Date.now(),pageT:0,tran:15};}," +
            "app:{isInstalled:false,getDetails:function(){},getIsInstalled:function(){},installState:function(){},runningState:function(){return 'cannot_run';}},tabs:{}" +
            "};}" +
            "try{Object.defineProperty(navigator,'plugins',{get:function(){var a=[{name:'Chrome PDF Plugin',filename:'internal-pdf-viewer',description:'Portable Document Format'},{name:'Chrome PDF Viewer',filename:'mhjfbmdgcfjbbpaeojofohoefgiehjai',description:''}];a.refresh=function(){};return a;},configurable:true});}catch(e){}" +
            "try{Object.defineProperty(navigator,'languages',{get:function(){return['en-IN','en-US','en'];},configurable:true});}catch(e){}" +
            "try{window.open=function(u){if(u&&typeof u==='string'&&u.indexOf('http')===0){window.location.href=u;}return window;};}catch(e){}" +
            "try{" +
            "var _gp1=WebGLRenderingContext.prototype.getParameter;" +
            "WebGLRenderingContext.prototype.getParameter=function(param){if(param===0x9245)return '" + safeVendor + "';if(param===0x9246)return '" + safeRenderer + "';return _gp1.apply(this,arguments);};" +
            "if(window.WebGL2RenderingContext){var _gp2=WebGL2RenderingContext.prototype.getParameter;WebGL2RenderingContext.prototype.getParameter=function(param){if(param===0x9245)return '" + safeVendor + "';if(param===0x9246)return '" + safeRenderer + "';return _gp2.apply(this,arguments);};}" +
            "}catch(e){}" +
            "try{" +
            "var _td=HTMLCanvasElement.prototype.toDataURL;" +
            "HTMLCanvasElement.prototype.toDataURL=function(){try{var c=this.getContext('2d');if(c&&this.width>0&&this.height>0){var d=c.getImageData(0,0,Math.min(8,this.width),Math.min(8,this.height));if(d&&d.data.length>4){d.data[" + canvasCh + "]=(d.data[" + canvasCh + "]^" + canvasXor + ");c.putImageData(d,0,0);}}}catch(e){}return _td.apply(this,arguments);};" +
            "}catch(e){}" +
            "try{" +
            "if(window.AudioBuffer&&AudioBuffer.prototype.getChannelData){var _acd=AudioBuffer.prototype.getChannelData;AudioBuffer.prototype.getChannelData=function(){var r=_acd.apply(this,arguments);try{if(r&&r.length>0&&!r.__fp){r.__fp=true;r[0]=r[0]+" + audioDelta + ";}}catch(e){}return r;};}" +
            "}catch(e){}" +
            "}catch(ex){}" +
            "})();";
    }

    private static final String SCRIPT_LITE = 

        "(function(){\n" +

        "    if (window.__lite_active) return;\n" +

        "\n" +

        "    function isCloudflareChallengePage() {\n" +

        "        try {\n" +

        "            var h = (location.hostname || '').toLowerCase();\n" +

        "            var p = (location.pathname || '').toLowerCase();\n" +

        "            var t = (document.title || '').toLowerCase();\n" +

        "            if (h.indexOf('cloudflare.com') !== -1 || p.indexOf('/cdn-cgi/') !== -1) return true;\n" +

        "            if (t.indexOf('just a moment') !== -1 || t.indexOf('attention required') !== -1) return true;\n" +

        "            if (document.querySelector('#challenge-form, #turnstile-wrapper, [name=\"cf-turnstile-response\"], script[src*=\"/cdn-cgi/challenge-platform/\"]')) return true;\n" +

        "        } catch(e){}\n" +

        "        return false;\n" +

        "    }\n" +

        "\n" +

        "    if (isCloudflareChallengePage()) {\n" +

        "        if (window.AndroidBridge && typeof window.AndroidBridge.onStatusUpdate === 'function') {\n" +

        "            window.AndroidBridge.onStatusUpdate('Cloudflare Check (complete manually)');\n" +

        "        }\n" +

        "        return;\n" +

        "    }\n" +

        "\n" +

        "    window.__lite_active = true;\n" +

        "\n" +

        "    function isAdNetwork(u) {\n" +

        "        if (!u) return false;\n" +

        "        var l = (u + '').toLowerCase();\n" +

        "        return l.indexOf('omg10.com') !== -1 || l.indexOf('popads') !== -1 || l.indexOf('adsterra') !== -1 || l.indexOf('bet365') !== -1 || l.indexOf('1xbet') !== -1 || l.indexOf('trafficjunky') !== -1 || l.indexOf('exoclick') !== -1 || l.indexOf('hilltopads') !== -1 || l.indexOf('propellerads') !== -1 || l.indexOf('clickadu') !== -1 || l.indexOf('monetag') !== -1 || l.indexOf('coinhive') !== -1 || l.indexOf('doubleclick') !== -1 || l.indexOf('adtrue') !== -1 || l.indexOf('syndication') !== -1;\n" +

        "    }\n" +

        "\n" +

        "    try {\n" +

        "        var _origReplace = window.location.replace.bind(window.location);\n" +

        "        window.location.replace = function(u) {\n" +

        "            if (isAdNetwork(u)) {\n" +

        "                if (window.AndroidBridge && typeof window.AndroidBridge.onRogueRedirectDetected === 'function') {\n" +

        "                    window.AndroidBridge.onRogueRedirectDetected(u);\n" +

        "                }\n" +

        "                return;\n" +

        "            }\n" +

        "            _origReplace(u);\n" +

        "        };\n" +

        "\n" +

        "        var _origOpen = window.open;\n" +

        "        window.open = function(u) {\n" +

        "            if (u && u !== 'about:blank' && !u.startsWith('javascript:')) {\n" +

        "                if (isAdNetwork(u)) {\n" +

        "                    if (window.AndroidBridge && typeof window.AndroidBridge.onRogueRedirectDetected === 'function') {\n" +

        "                        window.AndroidBridge.onRogueRedirectDetected(u);\n" +

        "                    }\n" +

        "                    return null;\n" +

        "                }\n" +

        "                window.location.href = u;\n" +

        "            }\n" +

        "            return {\n" +

        "                location: {\n" +

        "                    set href(newU) {\n" +

        "                        if (newU && newU !== 'about:blank' && !isAdNetwork(newU)) {\n" +

        "                            window.location.href = newU;\n" +

        "                        }\n" +

        "                    },\n" +

        "                    get href() { return window.location.href; }\n" +

        "                },\n" +

        "                closed: false,\n" +

        "                close: function(){}\n" +

        "            };\n" +

        "        };\n" +

        "    } catch(e){}\n" +

        "\n" +

        "    function getDynamicTargetDomain() {\n" +

        "        if (window.AndroidBridge && typeof window.AndroidBridge.getTargetDomain === 'function') {\n" +

        "            var bd = window.AndroidBridge.getTargetDomain();\n" +

        "            if (bd && bd.length > 2) return bd.toLowerCase();\n" +

        "        }\n" +

        "        try {\n" +

        "            var q = '';\n" +

        "            var p = new URLSearchParams(location.search);\n" +

        "            q = p.get('q') || '';\n" +

        "            if (!q) {\n" +

        "                var inp = document.querySelector('input[name=\"q\"], textarea[name=\"q\"]');\n" +

        "                if (inp && inp.value) q = inp.value;\n" +

        "            }\n" +

        "            var qLower = q.toLowerCase();\n" +

        "            var siteIdx = qLower.indexOf('site:');\n" +

        "            if (siteIdx === -1) siteIdx = qLower.indexOf('site%3a');\n" +

        "            if (siteIdx !== -1) {\n" +

        "                var rawD = q.substring(siteIdx + 5).trim().split('&')[0].split(' ')[0].split('/')[0];\n" +

        "                rawD = rawD.replace('https:', '').replace('http:', '').replace('//', '').trim().toLowerCase();\n" +

        "                if (rawD.startsWith('www.')) rawD = rawD.substring(4);\n" +

        "                if (rawD.length > 2) {\n" +

        "                    if (window.AndroidBridge && typeof window.AndroidBridge.setTargetDomain === 'function') {\n" +

        "                        window.AndroidBridge.setTargetDomain(rawD);\n" +

        "                    }\n" +

        "                    return rawD;\n" +

        "                }\n" +

        "            }\n" +

        "        } catch(e){}\n" +

        "        try {\n" +

        "            if (document.referrer) {\n" +

        "                var refHost = new URL(document.referrer).hostname.toLowerCase();\n" +

        "                if (refHost.startsWith('www.')) refHost = refHost.substring(4);\n" +

        "                if (refHost && refHost.indexOf('google.') === -1 && refHost.indexOf('liteshort.com') === -1) {\n" +

        "                    return refHost;\n" +

        "                }\n" +

        "            }\n" +

        "        } catch(e){}\n" +

        "        return '';\n" +

        "    }\n" +

        "\n" +

        "    var googleClickTriggered = false;\n" +

        "    function handleGoogleSearch() {\n" +

        "        try {\n" +

        "            var h = (location.hostname || '').toLowerCase();\n" +

        "            if (h.indexOf('google.') === -1) return false;\n" +

        "            if (googleClickTriggered) return true;\n" +

        "\n" +

        "            var targetDomain = getDynamicTargetDomain();\n" +

        "            if (window.AndroidBridge && typeof window.AndroidBridge.onStatusUpdate === 'function') {\n" +

        "                window.AndroidBridge.onStatusUpdate('Google: Target ' + (targetDomain || 'detecting...'));\n" +

        "            }\n" +

        "\n" +

        "            var anchors = document.querySelectorAll('a');\n" +

        "            for (var i = 0; i < anchors.length; i++) {\n" +

        "                var a = anchors[i];\n" +

        "                var rawHref = (a.getAttribute('href') || '') + ' ' + (a.href || '');\n" +

        "                var rawHrefLower = rawHref.toLowerCase();\n" +

        "                var textLower = ((a.innerText || '') + ' ' + (a.textContent || '')).toLowerCase();\n" +

        "\n" +

        "                if (rawHref.startsWith('/search?') && rawHref.indexOf('url?q=') === -1) continue;\n" +

        "                if (rawHrefLower.indexOf('google.com/search?') !== -1 && rawHrefLower.indexOf('url?q=') === -1) continue;\n" +

        "                if (rawHrefLower.indexOf('accounts.google') !== -1 || rawHrefLower.indexOf('support.google') !== -1) continue;\n" +

        "                if (rawHrefLower.indexOf('policies.google') !== -1 || rawHrefLower.indexOf('maps.google') !== -1) continue;\n" +

        "\n" +

        "                var isTarget = false;\n" +

        "                if (targetDomain) {\n" +

        "                    isTarget = (rawHrefLower.indexOf(targetDomain) !== -1 || textLower.indexOf(targetDomain) !== -1);\n" +

        "                } else {\n" +

        "                    if ((rawHrefLower.indexOf('http://') !== -1 || rawHrefLower.indexOf('https://') !== -1 || rawHrefLower.indexOf('/url?q=') !== -1) && rawHrefLower.indexOf('google.') === -1) {\n" +

        "                        isTarget = true;\n" +

        "                    }\n" +

        "                }\n" +

        "\n" +

        "                if (isTarget) {\n" +

        "                    if (textLower.indexOf('privacy policy') !== -1 || textLower.indexOf('contact us') !== -1 || textLower.indexOf('disclaimer') !== -1 || textLower.indexOf('page ') !== -1) {\n" +

        "                        continue;\n" +

        "                    }\n" +

        "\n" +

        "                    googleClickTriggered = true;\n" +

        "                    if (window.AndroidBridge && typeof window.AndroidBridge.onStatusUpdate === 'function') {\n" +

        "                        window.AndroidBridge.onStatusUpdate('Clicking Link: ' + (targetDomain || 'First Link'));\n" +

        "                    }\n" +

        "\n" +

        "                    var destUrl = a.href || a.getAttribute('href') || '';\n" +

        "                    if (destUrl.indexOf('url?q=') !== -1) {\n" +

        "                        try {\n" +

        "                            var part = destUrl.split('url?q=')[1];\n" +

        "                            if (part) {\n" +

        "                                destUrl = decodeURIComponent(part.split('&')[0]);\n" +

        "                            }\n" +

        "                        } catch(e) {}\n" +

        "                    }\n" +

        "\n" +

        "                    try {\n" +

        "                        a.style.outline = '3px solid #00E676';\n" +

        "                        a.focus();\n" +

        "                        a.dispatchEvent(new MouseEvent('mousedown', {bubbles: true, cancelable: true}));\n" +

        "                        a.dispatchEvent(new MouseEvent('mouseup', {bubbles: true, cancelable: true}));\n" +

        "                        a.click();\n" +

        "                    } catch(err) {}\n" +

        "\n" +

        "                    setTimeout(function() {\n" +

        "                        if (location.hostname.indexOf('google.') !== -1 && destUrl) {\n" +

        "                            location.href = destUrl;\n" +

        "                        }\n" +

        "                    }, 350);\n" +

        "\n" +

        "                    setTimeout(function() {\n" +

        "                        if (location.hostname.indexOf('google.') !== -1) {\n" +

        "                            googleClickTriggered = false;\n" +

        "                        }\n" +

        "                    }, 2500);\n" +

        "\n" +

        "                    return true;\n" +

        "                }\n" +

        "            }\n" +

        "\n" +

        "            if (targetDomain) {\n" +

        "                var elements = document.querySelectorAll('h3, cite, span, div');\n" +

        "                for (var j = 0; j < elements.length; j++) {\n" +

        "                    var el = elements[j];\n" +

        "                    var t = (el.innerText || el.textContent || '').toLowerCase();\n" +

        "                    if (t.indexOf(targetDomain) !== -1) {\n" +

        "                        var parentAnchor = el.closest('a');\n" +

        "                        if (parentAnchor) {\n" +

        "                            var tAnchor = ((parentAnchor.innerText || '') + ' ' + (parentAnchor.textContent || '')).toLowerCase();\n" +

        "                            if (tAnchor.indexOf('privacy policy') !== -1 || tAnchor.indexOf('contact us') !== -1) continue;\n" +

        "\n" +

        "                            googleClickTriggered = true;\n" +

        "                            if (window.AndroidBridge && typeof window.AndroidBridge.onStatusUpdate === 'function') {\n" +

        "                                window.AndroidBridge.onStatusUpdate('Clicking Card: ' + targetDomain);\n" +

        "                            }\n" +

        "                            try { parentAnchor.click(); } catch(e) {}\n" +

        "                            var pUrl = parentAnchor.href || parentAnchor.getAttribute('href');\n" +

        "                            if (pUrl && pUrl.indexOf('url?q=') !== -1) {\n" +

        "                                try {\n" +

        "                                    var p2 = pUrl.split('url?q=')[1];\n" +

        "                                    if (p2) pUrl = decodeURIComponent(p2.split('&')[0]);\n" +

        "                                } catch(e) {}\n" +

        "                            }\n" +

        "                            setTimeout(function() {\n" +

        "                                if (location.hostname.indexOf('google.') !== -1 && pUrl) {\n" +

        "                                    location.href = pUrl;\n" +

        "                                }\n" +

        "                            }, 350);\n" +

        "                            return true;\n" +

        "                        }\n" +

        "                    }\n" +

        "                }\n" +

        "            }\n" +

        "        } catch(e){}\n" +

        "        return false;\n" +

        "    }\n" +

        "\n" +

        "    function checkFastForward() {\n" +

        "        try {\n" +

        "            if (location.pathname.indexOf('new.php') !== -1 || document.getElementById('wpsafe-time')) {\n" +

        "                var d = (location.hostname || '').toLowerCase();\n" +

        "                if (d.startsWith('www.')) d = d.substring(4);\n" +

        "                if (d && window.AndroidBridge && typeof window.AndroidBridge.setTargetDomain === 'function') {\n" +

        "                    window.AndroidBridge.setTargetDomain(d);\n" +

        "                }\n" +

        "                if (window.AndroidBridge && typeof window.AndroidBridge.onStatusUpdate === 'function') {\n" +

        "                    window.AndroidBridge.onStatusUpdate('Fast forwarding to Google Search...');\n" +

        "                }\n" +

        "                if (typeof count !== 'undefined' && count > 0) {\n" +

        "                    count = 0;\n" +

        "                }\n" +

        "                var tm = document.getElementById('wpsafe-time');\n" +

        "                if (tm && parseInt(tm.innerText) > 0) {\n" +

        "                    tm.innerText = '0';\n" +

        "                }\n" +

        "            }\n" +

        "        } catch(e){}\n" +

        "    }\n" +

        "\n" +

        "    function handleLiteshortCloudTheme() {\n" +

        "        try {\n" +

        "            if (isCloudflareChallengePage()) return;\n" +

        "            var h = (location.hostname || '').toLowerCase();\n" +

        "            if (h.indexOf('liteshort.com') === -1) return;\n" +

        "            if (typeof app_vars !== 'undefined' && app_vars && (app_vars.enable_captcha === 'yes' || app_vars.captcha_shortlink === 'yes')) {\n" +

        "                if (!window.__ls_cap_notice && window.AndroidBridge && typeof window.AndroidBridge.onStatusUpdate === 'function') {\n" +

        "                    window.__ls_cap_notice = true;\n" +

        "                    window.AndroidBridge.onStatusUpdate('LiteShort: complete captcha manually');\n" +

        "                }\n" +

        "                return;\n" +

        "            }\n" +

        "            if (window.__ls_xhr_done) return;\n" +

        "            var goForm = document.getElementById('go-link');\n" +

        "            if (!goForm) return;\n" +

        "            var inputs = goForm.querySelectorAll('input, select, textarea');\n" +

        "            var csrfVal = null;\n" +

        "            var adVal = null;\n" +

        "            var pairs = [];\n" +

        "            var hasMethod = false;\n" +

        "            for (var i = 0; i < inputs.length; i++) {\n" +

        "                var inp = inputs[i];\n" +

        "                if (!inp.name || inp.disabled) continue;\n" +

        "                if ((inp.type === 'checkbox' || inp.type === 'radio') && !inp.checked) continue;\n" +

        "                if (inp.name === '_method') hasMethod = true;\n" +

        "                if (inp.name === '_csrfToken') csrfVal = inp.value;\n" +

        "                if (inp.name === 'ad_form_data') adVal = inp.value;\n" +

        "                pairs.push(encodeURIComponent(inp.name) + '=' + encodeURIComponent(inp.value || ''));\n" +

        "            }\n" +

        "            if (!csrfVal || !adVal) return;\n" +

        "            if (!hasMethod) pairs.unshift('_method=POST');\n" +

        "            window.__ls_xhr_done = true;\n" +

        "            if (window.AndroidBridge && typeof window.AndroidBridge.onStatusUpdate === 'function') {\n" +

        "                window.AndroidBridge.onStatusUpdate('LiteShort: posting links/go...');\n" +

        "            }\n" +

        "            var body = pairs.join('&');\n" +

        "            var actionUrl = goForm.getAttribute('action') || '/links/go';\n" +

        "            var xhrGo = new XMLHttpRequest();\n" +

        "            xhrGo.open('POST', actionUrl, true);\n" +

        "            xhrGo.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded; charset=UTF-8');\n" +

        "            xhrGo.setRequestHeader('X-Requested-With', 'XMLHttpRequest');\n" +

        "            xhrGo.setRequestHeader('Accept', 'application/json, text/javascript, */*; q=0.01');\n" +

        "            xhrGo.onload = function() {\n" +

        "                try {\n" +

        "                    var r = JSON.parse(xhrGo.responseText);\n" +

        "                    if (r && r.status === 'success' && r.url) {\n" +

        "                        if (window.AndroidBridge && typeof window.AndroidBridge.onStatusUpdate === 'function') {\n" +

        "                            window.AndroidBridge.onStatusUpdate('LiteShort: got link!');\n" +

        "                        }\n" +

        "                        setTimeout(function(){ window.location.href = r.url; }, 200);\n" +

        "                        return;\n" +

        "                    }\n" +

        "                    if (r && r.message && window.AndroidBridge && typeof window.AndroidBridge.onStatusUpdate === 'function') {\n" +

        "                        window.AndroidBridge.onStatusUpdate('LiteShort: ' + r.message);\n" +

        "                    }\n" +

        "                } catch(e) {}\n" +

        "                window.__ls_xhr_tries = (window.__ls_xhr_tries || 0) + 1;\n" +

        "                if (window.__ls_xhr_tries < 4) {\n" +

        "                    setTimeout(function(){ window.__ls_xhr_done = false; }, 2500);\n" +

        "                }\n" +

        "            };\n" +

        "            xhrGo.onerror = function() {\n" +

        "                window.__ls_xhr_tries = (window.__ls_xhr_tries || 0) + 1;\n" +

        "                if (window.__ls_xhr_tries < 4) {\n" +

        "                    setTimeout(function(){ window.__ls_xhr_done = false; }, 2000);\n" +

        "                }\n" +

        "            };\n" +

        "            xhrGo.send(body);\n" +

        "        } catch(ex) {}\n" +

        "    }\n" +

        "\n" +

        "    function handleRedirectLink() {\n" +

        "        try {\n" +

        "            if (window.__dj_fired) return;\n" +

        "            var redir = document.getElementById('redirectLink');\n" +

        "            if (!redir) return;\n" +

        "            var dest = redir.href || redir.getAttribute('href') || '';\n" +

        "            if (!dest || dest.indexOf('http') !== 0) return;\n" +

        "            if (dest.indexOf('ey43') !== -1 || dest.indexOf('cdn-cgi') !== -1) return;\n" +

        "            window.__dj_fired = true;\n" +

        "            if (window.AndroidBridge && typeof window.AndroidBridge.onStatusUpdate === 'function') {\n" +

        "                window.AndroidBridge.onStatusUpdate('Blog: navigating to destination...');\n" +

        "            }\n" +

        "            setTimeout(function(){ window.location.href = dest; }, 100);\n" +

        "        } catch(e) {}\n" +

        "    }\n" +

        "\n" +

        "    // dj's Direct Script: Embedded directly, zero external network dependency\n" +

        "    function runDjMainScript() {\n" +

        "        try {\n" +

        "            var h = (location.hostname || '').toLowerCase();\n" +

        "            var p = (location.pathname || '').toLowerCase();\n" +

        "            if (h.indexOf('google.') !== -1) return;\n" +

        "            if (p.indexOf('new.php') !== -1) return;\n" +

        "            if (isCloudflareChallengePage()) return;\n" +

        "            if (h.indexOf('mediafire.com') !== -1) return;\n" +

        "\n" +

        "            const d = document, $ = s => [...d.querySelectorAll(s)];\n" +

        "            try { d.cookie = 'ad_expire=true;path=/;max-age=86400'; } catch(e){}\n" +

        "\n" +

        "            $('.overlay,#overlay,#popup,span.countdown,#timer').forEach(e => e.style.setProperty('display','none','important'));\n" +

        "            $('button[disabled],input[disabled]').forEach(e => e.disabled = false);\n" +

        "            $('#getlink,#getlink1,#rtg-snp2,#rtg-snp21,#notarobot,[id^=\"rtg\"]').forEach(e => e.style.setProperty('display','inline-block','important'));\n" +

        "            d.documentElement.style.setProperty('overflow','auto','important');\n" +

        "            if (d.body) d.body.style.setProperty('overflow','auto','important');\n" +

        "\n" +

        "            // Scroll Down / Tap Scroll Down auto click\n" +

        "            try {\n" +

        "                const scrollBtn = [...d.querySelectorAll('button, a, input, div[role=\"button\"]')].find(e => {\n" +

        "                    const t = ((e.innerText || '') + ' ' + (e.value || '') + ' ' + (e.textContent || '')).toLowerCase();\n" +

        "                    return t.includes('scroll down');\n" +

        "                });\n" +

        "                if (scrollBtn) scrollBtn.click();\n" +

        "            } catch(err){}\n" +

        "\n" +

        "            if (window.__dj_fired) return;\n" +

        "            handleRedirectLink();\n" +

        "            if (window.__dj_fired) return;\n" +

        "            const curUrl = location.href;\n" +

        "            const snpBtn = d.querySelector('#rtg-snp2');\n" +

        "            const a = snpBtn ? (snpBtn.closest('a') || (snpBtn.tagName === 'A' ? snpBtn : null)) : null,\n" +

        "                  f = d.querySelector('#rtg-snp21 form,#notarobot form,form#rtg'),\n" +

        "                  b = d.querySelector('#getlink1,#getlink');\n" +

        "            const fReady = f && (d.readyState !== 'loading' || !!f.querySelector('button,input[type="submit"]'));\n" +

        "            if (a) {\n" +

        "                window.__dj_fired = true;\n" +

        "                if (window.AndroidBridge && typeof window.AndroidBridge.onStatusUpdate === 'function') {\n" +

        "                    window.AndroidBridge.onStatusUpdate('Clicking rtg-snp2...');\n" +

        "                }\n" +

        "                try { a.click(); } catch(err) {}\n" +

        "                setTimeout(function() {\n" +

        "                    try {\n" +

        "                        if (location.href === curUrl && a.href && a.href.indexOf('http') === 0) {\n" +

        "                            window.location.href = a.href;\n" +

        "                        }\n" +

        "                    } catch(err) {}\n" +

        "                }, 450);\n" +

        "            } else if (fReady) {\n" +

        "                window.__dj_fired = true;\n" +

        "                if (window.AndroidBridge && typeof window.AndroidBridge.onStatusUpdate === 'function') {\n" +

        "                    window.AndroidBridge.onStatusUpdate('Submitting form...');\n" +

        "                }\n" +

        "                try {\n" +

        "                    HTMLFormElement.prototype.submit.call(f);\n" +

        "                } catch(err) {\n" +

        "                    try { f.requestSubmit ? f.requestSubmit() : f.submit(); } catch(e2) {}\n" +

        "                }\n" +

        "                setTimeout(function() {\n" +

        "                    if (location.href === curUrl) window.__dj_fired = false;\n" +

        "                }, 3500);\n" +

        "            } else if (b && !window.__dj_getlink_clicked) {\n" +

        "                window.__dj_getlink_clicked = true;\n" +

        "                if (window.AndroidBridge && typeof window.AndroidBridge.onStatusUpdate === 'function') {\n" +

        "                    window.AndroidBridge.onStatusUpdate('Clicking Get Link...');\n" +

        "                }\n" +

        "                try { b.click(); } catch(err) {}\n" +

        "            } else if (!window.__dj_getlink_clicked) {\n" +

        "                if (window.AndroidBridge && typeof window.AndroidBridge.onStatusUpdate === 'function') {\n" +

        "                    window.AndroidBridge.onStatusUpdate('Script Active: ' + location.hostname);\n" +

        "                }\n" +

        "            }\n" +

        "        } catch(e){}\n" +

        "    }\n" +

        "\n" +

        "    function checkMediafire() {\n" +

        "        try {\n" +

        "            var h = (location.href || '').toLowerCase();\n" +

        "            if (h.indexOf('mediafire.com') !== -1 || document.querySelector('a[href*=\"mediafire.com\"]')) {\n" +

        "                if (window.AndroidBridge && typeof window.AndroidBridge.onMediafireDetected === 'function') {\n" +

        "                    window.AndroidBridge.onMediafireDetected();\n" +

        "                    return true;\n" +

        "                }\n" +

        "            }\n" +

        "        } catch(e){}\n" +

        "        return false;\n" +

        "    }\n" +

        "\n" +

        "    function checkKnownRogueAd() {\n" +

        "        try {\n" +

        "            var h = (location.hostname || '').toLowerCase();\n" +

        "            var href = (location.href || '').toLowerCase();\n" +

        "            var target = getDynamicTargetDomain();\n" +

        "            if (h.indexOf('google.') !== -1 || h.indexOf('liteshort.com') !== -1 || h.indexOf('mediafire.com') !== -1 || h.indexOf('cloudflare.com') !== -1 || href.indexOf('/cdn-cgi/') !== -1) {\n" +

        "                return false;\n" +

        "            }\n" +

        "            if (target && (h.indexOf(target) !== -1 || href.indexOf(target) !== -1)) {\n" +

        "                return false;\n" +

        "            }\n" +

        "            if (h.indexOf('gadinow.in') !== -1 || h.indexOf('telugujobzhub.in') !== -1 || h.indexOf('portalresult') !== -1) {\n" +

        "                return false;\n" +

        "            }\n" +

        "            if (isAdNetwork(h) || isAdNetwork(href)) {\n" +

        "                if (window.AndroidBridge && typeof window.AndroidBridge.onRogueRedirectDetected === 'function') {\n" +

        "                    window.AndroidBridge.onRogueRedirectDetected(location.href);\n" +

        "                    return true;\n" +

        "                }\n" +

        "            }\n" +

        "        } catch(e){}\n" +

        "        return false;\n" +

        "    }\n" +

        "\n" +

        "    handleLiteshortCloudTheme();\n" +

        "    handleRedirectLink();\n" +

        "    handleGoogleSearch();\n" +

        "    checkFastForward();\n" +

        "    runDjMainScript();\n" +

        "    checkMediafire();\n" +

        "    checkKnownRogueAd();\n" +

        "\n" +

        "    setInterval(handleLiteshortCloudTheme, 400);\n" +

        "    setInterval(handleRedirectLink, 300);\n" +

        "    setInterval(handleGoogleSearch, 300);\n" +

        "    setInterval(checkFastForward, 300);\n" +

        "    setInterval(runDjMainScript, 250);\n" +

        "    setInterval(checkMediafire, 500);\n" +

        "    setInterval(checkKnownRogueAd, 800);\n" +

        "\n" +

        "    try {\n" +

        "        var observer = new MutationObserver(function() {\n" +

        "            handleGoogleSearch();\n" +

        "            runDjMainScript();\n" +

        "            checkMediafire();\n" +

        "            checkKnownRogueAd();\n" +

        "        });\n" +

        "        if (document.body) {\n" +

        "            observer.observe(document.body, { childList: true, subtree: true, characterData: true });\n" +

        "        }\n" +

        "    } catch(e){}\n" +

        "})();";



    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private boolean isRotating = false;

    private boolean isMediafireTriggering = false;

    private boolean isResetting = false;

    private boolean isHandlingError = false;

    private String currentIp = "...";

    private String previousIp = "";

    private volatile String dynamicTargetDomain = "";



    // 120-Second Watchdog Timer

    private final Runnable watchdogRunnable = new Runnable() {

        @Override

        public void run() {

            if (!isRotating && !isMediafireTriggering && !isResetting) {

                mainHandler.post(() -> {

                    tvStatus.setTextColor(Color.parseColor("#FFA500"));

                    tvStatus.setText("Watchdog: timeout! Rotating IP...");

                    triggerRogueRedirectReset("timeout_120s");

                });

            }

        }

    };



    private void armWatchdog() {

        mainHandler.removeCallbacks(watchdogRunnable);

        mainHandler.postDelayed(watchdogRunnable, 120000);

    }



    private void disarmWatchdog() {

        mainHandler.removeCallbacks(watchdogRunnable);

    }



    public class WebAppInterface {

        @JavascriptInterface

        public void onMediafireDetected() {

            runOnUiThread(() -> checkAndHandleMediafire("https://www.mediafire.com"));

        }



        @JavascriptInterface

        public void onRogueRedirectDetected(String rogueUrl) {

            // Silently drop rogue ad attempts without resetting the shortener

        }



        @JavascriptInterface

        public void setTargetDomain(String domain) {

            if (domain != null && !domain.isEmpty()) {

                dynamicTargetDomain = domain.toLowerCase().trim();

            }

        }



        @JavascriptInterface

        public String getTargetDomain() {

            return dynamicTargetDomain != null ? dynamicTargetDomain : "";

        }



        @JavascriptInterface

        public void onStatusUpdate(String status) {

            runOnUiThread(() -> {

                if (!isRotating && !isMediafireTriggering && !isResetting && status != null) {

                    armWatchdog();

                    tvStatus.setTextColor(Color.parseColor("#00E676"));

                    tvStatus.setText(status);

                }

            });

        }

    }



    @SuppressLint("SetJavaScriptEnabled")

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);



        btnAirplane = findViewById(R.id.btn_airplane);

        btnReload = findViewById(R.id.btn_reload);

        tvStatus = findViewById(R.id.tv_status);

        tvIp = findViewById(R.id.tv_ip);

        progressBar = findViewById(R.id.progress_bar);

        webView = findViewById(R.id.webview);



        webView.setBackgroundColor(Color.WHITE);

        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);



        // Keep CPU awake when screen is locked

        try {

            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

            if (pm != null) {

                wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Lite:WakeLock");

                wakeLock.acquire();

            }

        } catch (Exception ignored) {}



        CookieManager cookieManager = CookieManager.getInstance();

        cookieManager.setAcceptCookie(true);

        cookieManager.setAcceptThirdPartyCookies(webView, true);



        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);

        settings.setDomStorageEnabled(true);

        settings.setDatabaseEnabled(true);

        settings.setAllowContentAccess(true);

        settings.setAllowFileAccess(true);

        settings.setLoadsImagesAutomatically(true);

        settings.setSupportMultipleWindows(false);

        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        settings.setUseWideViewPort(true);

        settings.setLoadWithOverviewMode(true);

        settings.setCacheMode(WebSettings.LOAD_DEFAULT);

        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);



        // Suppress X-Requested-With header so request headers match standalone Chrome

        if (WebViewFeature.isFeatureSupported(WebViewFeature.REQUESTED_WITH_HEADER_ALLOW_LIST)) {

            WebSettingsCompat.setRequestedWithHeaderOriginAllowList(settings, Collections.emptySet());

        }



        // Initialize per-cycle device profile & User-Agent
        rotateDeviceProfile();
        try {
            settings.setUserAgentString(activeProfile.ua);
        } catch (Exception ignored) {
            String baseUa = WebSettings.getDefaultUserAgent(this);
            String cleanUa = baseUa.replace("; wv", "").replaceAll("Version/\\d+\\.\\d+\\s*", "");
            settings.setUserAgentString(cleanUa);
        }

        webView.addJavascriptInterface(new WebAppInterface(), "AndroidBridge");



        webView.setWebChromeClient(new WebChromeClient() {

            @Override

            public void onProgressChanged(WebView view, int newProgress) {

                if (newProgress < 100) {

                    progressBar.setVisibility(View.VISIBLE);

                } else {

                    progressBar.setVisibility(View.GONE);

                }

                String curUrl = view.getUrl();

                if (newProgress >= 15 && curUrl != null && !curUrl.equals("about:blank")) {

                    view.evaluateJavascript(currentSpoofJs, null);

                    if (!curUrl.contains("/cdn-cgi/") && !curUrl.contains("challenges.cloudflare.com")) {

                        view.evaluateJavascript(SCRIPT_LITE, null);

                    }

                }

            }



            @Override

            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {

                result.confirm();

                return true;

            }



            @Override

            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {

                result.confirm();

                return true;

            }



            @Override

            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {

                result.confirm();

                return true;

            }

        });



        webView.setWebViewClient(new WebViewClient() {

            @SuppressLint("WebViewClientOnReceivedSslError")

            @Override

            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {

                handler.proceed();

            }



            @Override

            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

                return handleUrl(view, request.getUrl().toString());

            }



            @Override

            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                return handleUrl(view, url);

            }



            @Override

            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                super.onPageStarted(view, url, favicon);

                if (url != null && !url.equals("about:blank")) {

                    // Chrome spoof first on every page — CF needs it before its JS runs
                    view.evaluateJavascript(currentSpoofJs, null);

                    if (url.contains("/cdn-cgi/") || url.contains("challenges.cloudflare.com")) {

                        tvStatus.setTextColor(Color.parseColor("#FFA500"));

                        tvStatus.setText("Cloudflare Check (complete manually)");

                        armWatchdog();

                        return;

                    }

                    if (checkAndHandleMediafire(url)) return;

                    if (isKnownRogueAdUrl(url)) {

                        return;

                    }

                    view.evaluateJavascript(SCRIPT_LITE, null);

                }

            }



            @Override

            public void onLoadResource(WebView view, String url) {

                super.onLoadResource(view, url);

                if (url != null && !url.equals("about:blank")) {

                    checkAndHandleMediafire(url);

                }

            }



            @Override

            public void onPageFinished(WebView view, String url) {

                super.onPageFinished(view, url);

                progressBar.setVisibility(View.GONE);

                if (url == null || url.equals("about:blank")) return;



                view.evaluateJavascript(currentSpoofJs, null);

                if (url.contains("/cdn-cgi/") || url.contains("challenges.cloudflare.com")) {

                    tvStatus.setTextColor(Color.parseColor("#FFA500"));

                    tvStatus.setText("Cloudflare Check (complete manually)");

                    return;

                }



                if (checkAndHandleMediafire(url)) return;



                if (isKnownRogueAdUrl(url)) {

                    triggerRogueRedirectReset(url);

                    return;

                }



                view.evaluateJavascript(SCRIPT_LITE, null);

            }



            @Override

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

                super.onReceivedError(view, errorCode, description, failingUrl);

                handleConnectionError();

            }



            @Override

            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {

                super.onReceivedError(view, request, error);

                if (request != null && request.isForMainFrame()) {

                    handleConnectionError();

                }

            }

        });



        btnAirplane.setOnClickListener(v -> startAirplaneReset());

        btnReload.setOnClickListener(v -> refreshIpAndLoad(true));



        updateIpAddress();

        refreshIpAndLoad(true);

    }



    private void handleConnectionError() {

        if (isRotating || isResetting || isHandlingError || isMediafireTriggering) return;

        isHandlingError = true;



        new Thread(() -> {

            for (int i = 0; i < 40; i++) {

                try { Thread.sleep(700); } catch (InterruptedException ignored) {}

                if (isInternetAvailable()) {

                    break;

                }

            }

            try { Thread.sleep(500); } catch (InterruptedException ignored) {}

            mainHandler.post(() -> {

                isHandlingError = false;

                if (!isRotating && !isResetting && !isMediafireTriggering) {

                    if (webView != null) webView.reload();

                }

            });

        }).start();

    }



    private boolean isKnownRogueAdUrl(String url) {

        if (url == null) return false;

        String lower = url.toLowerCase();

        // NEVER consider Google, telugujobzhub, portalresult, liteshort, cloudflare, or mediafire as rogue

        if (lower.contains("google.") || lower.contains("liteshort.com") || lower.contains("mediafire.com") ||

            lower.contains("cloudflare.com") || lower.contains("/cdn-cgi/")) {

            return false;

        }

        if (!dynamicTargetDomain.isEmpty() && lower.contains(dynamicTargetDomain)) {

            return false;

        }

        if (lower.contains("gadinow.in") || lower.contains("telugujobzhub.in") || lower.contains("portalresult")) {

            return false;

        }

        return lower.contains("omg10.com") ||

               lower.contains("popads") ||

               lower.contains("adsterra") ||

               lower.contains("bet365") ||

               lower.contains("1xbet") ||

               lower.contains("trafficjunky") ||

               lower.contains("exoclick") ||

               lower.contains("hilltopads") ||

               lower.contains("propellerads") ||

               lower.contains("clickadu") ||

               lower.contains("monetag") ||

               lower.contains("coinhive") ||

               lower.contains("doubleclick") ||

               lower.contains("adtrue") ||

               lower.contains("syndication") ||

               lower.contains("ey43.com") ||

               lower.contains("ouo.io") ||

               lower.contains("shrinkme.io");

    }



    private boolean handleUrl(WebView view, String url) {

        if (url == null) return false;

        String lower = url.toLowerCase();

        if (checkAndHandleMediafire(url)) {

            return true;

        }

        if (isKnownRogueAdUrl(url)) {

            mainHandler.post(() -> {

                tvStatus.setText("Blocked ad: " + (url.length() > 25 ? url.substring(0, 25) + "..." : url));

            });

            return true;

        }



        // Handle Android Chrome Intent schemes (intent://lucky.telugujobzhub.in/...)

        if (url.startsWith("intent://")) {

            try {

                Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);

                String fallbackUrl = intent.getStringExtra("browser_fallback_url");

                if (fallbackUrl != null && !fallbackUrl.isEmpty()) {

                    view.loadUrl(fallbackUrl);

                    return true;

                }

                String raw = url.substring("intent://".length());

                int hashIdx = raw.indexOf("#Intent;");

                if (hashIdx != -1) raw = raw.substring(0, hashIdx);

                String scheme = url.contains("scheme=http;") ? "http" : "https";

                String dest = scheme + "://" + raw;

                try {

                    android.net.Uri u = android.net.Uri.parse(dest);

                    if (u.getHost() != null) {

                        dynamicTargetDomain = u.getHost().toLowerCase().replace("www.", "");

                    }

                } catch (Exception ignored) {}

                view.loadUrl(dest);

                return true;

            } catch (Exception ignored) {}

        }



        if (url.startsWith("http://") || url.startsWith("https://")) {

            return false;

        }

        return true;

    }



    // Rogue Redirect Reset

    private synchronized void triggerRogueRedirectReset(String rogueUrl) {

        if (isRotating || isMediafireTriggering || isResetting) return;

        isResetting = true;

        disarmWatchdog();



        mainHandler.post(() -> {

            try {

                if (webView != null) {

                    webView.stopLoading();

                    webView.loadUrl("about:blank");

                }

            } catch (Exception ignored) {}



            tvStatus.setTextColor(Color.parseColor("#FF5252"));

            tvStatus.setText("Resetting...");

            Toast.makeText(MainActivity.this, "Resetting shortener...", Toast.LENGTH_SHORT).show();



            mainHandler.postDelayed(() -> {

                isResetting = false;

                startAirplaneReset();

            }, 1500);

        });

    }



    private synchronized boolean checkAndHandleMediafire(String url) {

        if (url == null) return false;

        String lower = url.toLowerCase();

        if (lower.contains("mediafire.com")) {

            if (isRotating || isMediafireTriggering || isResetting) {

                return true;

            }

            isMediafireTriggering = true;

            disarmWatchdog();



            mainHandler.post(() -> {

                try {

                    if (webView != null) {

                        webView.stopLoading();

                        webView.loadUrl("about:blank");

                    }

                } catch (Exception ignored) {}



                wipeSession();



                tvStatus.setTextColor(Color.parseColor("#00E676"));

                tvStatus.setText("MediaFire Detected! Rotating IP...");

                Toast.makeText(MainActivity.this, "MediaFire Detected! Rotating IP...", Toast.LENGTH_SHORT).show();



                mainHandler.postDelayed(() -> {

                    isMediafireTriggering = false;

                    startAirplaneReset();

                }, 2000);

            });

            return true;

        }

        return false;

    }



    private boolean runRootCommand(String command) {

        try {

            Process p = Runtime.getRuntime().exec("su");

            DataOutputStream os = new DataOutputStream(p.getOutputStream());

            os.writeBytes(command + "\n");

            os.writeBytes("exit\n");

            os.flush();

            int exit = p.waitFor();

            return (exit == 0);

        } catch (Exception ignored) {

            return false;

        }

    }



    private void updateIpAddress() {

        new Thread(() -> {

            String ip = fetchPublicIp();

            mainHandler.post(() -> {

                currentIp = ip;

                tvIp.setText("IP: " + ip);

            });

        }).start();

    }



    private String fetchPublicIp() {

        String[] endpoints = new String[]{

            "https://api.ipify.org",

            "https://icanhazip.com",

            "https://checkip.amazonaws.com"

        };

        for (String endpoint : endpoints) {

            try {

                URL url = new URL(endpoint);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setConnectTimeout(4000);

                conn.setReadTimeout(4000);

                conn.setRequestMethod("GET");

                if (conn.getResponseCode() == 200) {

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    String ip = reader.readLine();

                    reader.close();

                    if (ip != null && !ip.trim().isEmpty()) {

                        return ip.trim();

                    }

                }

            } catch (Exception ignored) {}

        }

        return "Unknown";

    }



    private void setButtonsEnabled(boolean enabled) {

        btnAirplane.setEnabled(enabled);

        btnReload.setEnabled(enabled);

    }



    private void startAirplaneReset() {

        if (isRotating) return;

        isRotating = true;

        isMediafireTriggering = false;

        isResetting = false;

        isHandlingError = false;

        disarmWatchdog();



        mainHandler.post(() -> {

            setButtonsEnabled(false);

            tvStatus.setTextColor(Color.parseColor("#FFA500"));

            tvStatus.setText("Airplane ON...");

        });



        new Thread(() -> {

            // Proven root airplane toggle (works across Android 11, 12, 13, 14)

            runRootCommand("cmd connectivity airplane-mode enable || settings put global airplane_mode_on 1");



            // Randomize Android ID silently via root during airplane mode toggle

            try {

                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < 16; i++) {

                    sb.append(Integer.toHexString(RANDOM.nextInt(16)));

                }

                String newAid = sb.toString();
                runRootCommand("settings put secure android_id " + newAid + "; content call --uri content://settings/secure --method PUT_secure --arg android_id --extra value:s:" + newAid + "; rm -f /data/data/com.google.android.gms/shared_prefs/adid_settings.xml");

            } catch (Exception ignored) {}



            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}



            mainHandler.post(() -> tvStatus.setText("Airplane OFF..."));

            runRootCommand("cmd connectivity airplane-mode disable || settings put global airplane_mode_on 0");



            mainHandler.post(() -> tvStatus.setText("Connecting 4G..."));



            boolean connected = false;

            for (int i = 0; i < 45; i++) {

                try { Thread.sleep(500); } catch (InterruptedException ignored) {}

                if (isInternetAvailable()) {

                    connected = true;

                    break;

                }

            }



            if (!connected) {

                mainHandler.post(() -> tvStatus.setText("Awaiting 4G..."));

                try { Thread.sleep(3000); } catch (InterruptedException ignored) {}

            } else {

                try { Thread.sleep(500); } catch (InterruptedException ignored) {}

            }



            String newIp = fetchPublicIp();



            for (int ipRetry = 1; ipRetry <= 3; ipRetry++) {
                if (previousIp.isEmpty() || previousIp.equals("...") || previousIp.equals("Connected") || previousIp.equals("Unknown") || !newIp.equals(previousIp)) {
                    break;
                }
                final int retryNum = ipRetry;
                mainHandler.post(() -> tvStatus.setText("Same IP! Re-toggling Airplane (" + retryNum + "/3)..."));
                runRootCommand("cmd connectivity airplane-mode enable || settings put global airplane_mode_on 1");
                try { Thread.sleep(2500); } catch (InterruptedException ignored) {}
                runRootCommand("cmd connectivity airplane-mode disable || settings put global airplane_mode_on 0");
                for (int i = 0; i < 40; i++) {
                    try { Thread.sleep(500); } catch (InterruptedException ignored) {}
                    if (isInternetAvailable()) break;
                }
                try { Thread.sleep(500); } catch (InterruptedException ignored) {}
                newIp = fetchPublicIp();
            }

            previousIp = newIp;



            final String finalIp = newIp;

            mainHandler.post(() -> {

                currentIp = finalIp;

                tvIp.setText("IP: " + finalIp);

                tvStatus.setText("Ready");

                setButtonsEnabled(true);

                isRotating = false;



                refreshIpAndLoad(true);

            });

        }).start();

    }



    private boolean isInternetAvailable() {

        try {

            HttpURLConnection conn = (HttpURLConnection) (new URL("https://connectivitycheck.gstatic.com/generate_204").openConnection());

            conn.setRequestProperty("User-Agent", "Android");

            conn.setConnectTimeout(1500);

            conn.setReadTimeout(1500);

            conn.connect();

            return (conn.getResponseCode() == 204);

        } catch (Exception e) {

            return false;

        }

    }



    private void wipeSession() {

        try {

            CookieManager.getInstance().removeAllCookies(null);

            CookieManager.getInstance().flush();

            WebStorage.getInstance().deleteAllData();

            if (webView != null) {

                webView.clearHistory();

                webView.clearCache(true);

                webView.clearFormData();

                webView.clearSslPreferences();

            }

        } catch (Exception ignored) {}

    }



    private void refreshIpAndLoad(boolean reloadWeb) {

        wipeSession();

        armWatchdog();



        // Rotate full device profile (UA + GPU + CPU/RAM + Canvas/Audio seeds) for new cycle
        rotateDeviceProfile();
        try {
            if (webView != null) {
                webView.getSettings().setUserAgentString(activeProfile.ua);
            }
        } catch (Exception ignored) {
            String baseUa = WebSettings.getDefaultUserAgent(this);
            String cleanUa = baseUa.replace("; wv", "").replaceAll("Version/\\d+\\.\\d+\\s*", "");
            if (webView != null) {
                webView.getSettings().setUserAgentString(cleanUa);
            }
        }

        if (reloadWeb && webView != null) {

            tvStatus.setTextColor(Color.parseColor("#29B6F6"));

            tvStatus.setText("Loading Shortener...");

            webView.loadUrl(TARGET_URL);

        }

    }



    @Override

    protected void onPause() {

        super.onPause();

        if (webView != null) {

            webView.resumeTimers();

        }

    }



    @Override

    protected void onStop() {

        super.onStop();

        if (webView != null) {

            webView.resumeTimers();

        }

    }



    @Override

    protected void onDestroy() {

        disarmWatchdog();

        try {

            if (wakeLock != null && wakeLock.isHeld()) {

                wakeLock.release();

            }

        } catch (Exception ignored) {}

        super.onDestroy();

    }

}

