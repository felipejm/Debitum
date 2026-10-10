#!/usr/bin/env python3
"""Generates the Debitum launcher icons for Android and iOS from vector geometry.

Requires rsvg-convert and ImageMagick (magick). Run from anywhere:
    python3 tools/icon/generate_icons.py
"""
import json
import os
import subprocess
import tempfile

ROOT = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", ".."))
ANDROID_RES = os.path.join(ROOT, "androidApp", "src", "main", "res")
IOS_ICONSET = os.path.join(ROOT, "iosApp", "iosApp", "Assets.xcassets", "AppIcon.appiconset")

BACKGROUND = "#2B1E2F"
LETTER = "#FFFFFF"
CHECK = "#D06E55"

# Logo geometry, in design units. Bounding box: x 233..581, y 277..570.
LOGO_CENTER = (407.0, 423.5)
LOGO_WIDTH = 348.0
D_PATH = (
    "M233,277 H370 A141,146.5 0 0 1 370,570 H233 Z "
    "M294,325 H350 A105,95 0 0 1 350,515 H294 Z"
)
CHECK_PATH = "M244,394 H317 L353,430 L506,277 H579 L353,503 Z"
# Check outline grown by the gap that separates it from the letter (counter-clockwise).
CHECK_GAP_POINTS = [(206, 378), (343, 515), (363, 515), (617, 261), (500, 261), (353, 408), (323, 378)]
CLIP_PATH = (
    "M-2000,-2000 H3000 V3000 H-2000 Z "
    "M" + " L".join(f"{x},{y}" for x, y in CHECK_GAP_POINTS) + " Z"
)


def logo_transform(canvas, width_fraction):
    scale = canvas * width_fraction / LOGO_WIDTH
    cx, cy = LOGO_CENTER
    return scale, canvas / 2 - cx * scale, canvas / 2 - cy * scale


def svg(canvas, width_fraction, background, letter, check, shape=None):
    scale, tx, ty = logo_transform(canvas, width_fraction)
    if shape == "circle":
        clip = f'<circle cx="{canvas / 2}" cy="{canvas / 2}" r="{canvas / 2}"/>'
    elif shape == "rounded":
        r = canvas * 0.18
        clip = f'<rect width="{canvas}" height="{canvas}" rx="{r}" ry="{r}"/>'
    else:
        clip = f'<rect width="{canvas}" height="{canvas}"/>'
    bg = f'<rect width="{canvas}" height="{canvas}" fill="{background}"/>' if background else ""
    return f"""<svg xmlns="http://www.w3.org/2000/svg" width="{canvas}" height="{canvas}" viewBox="0 0 {canvas} {canvas}">
  <defs>
    <clipPath id="shape">{clip}</clipPath>
    <clipPath id="gap" clipPathUnits="userSpaceOnUse"><path d="{CLIP_PATH}"/></clipPath>
  </defs>
  <g clip-path="url(#shape)">
    {bg}
    <g transform="translate({tx} {ty}) scale({scale})">
      <path d="{D_PATH}" fill="{letter}" fill-rule="evenodd" clip-path="url(#gap)"/>
      <path d="{CHECK_PATH}" fill="{check}"/>
    </g>
  </g>
</svg>
"""


def render(svg_text, out_png, size, opaque=False):
    with tempfile.NamedTemporaryFile("w", suffix=".svg", delete=False) as f:
        f.write(svg_text)
        src = f.name
    subprocess.run(["rsvg-convert", "-w", str(size), "-h", str(size), "-o", out_png, src], check=True)
    os.remove(src)
    if opaque:
        subprocess.run(["magick", out_png, "-background", BACKGROUND, "-alpha", "remove", "-alpha", "off", out_png], check=True)


# Android adaptive icon: 108dp canvas, logo kept inside the 66dp safe zone.
ANDROID_CANVAS = 108
ANDROID_WIDTH_FRACTION = 50 / 108


def vector_drawable(letter, check):
    scale, _, _ = logo_transform(ANDROID_CANVAS, ANDROID_WIDTH_FRACTION)
    cx, cy = LOGO_CENTER
    return f"""<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="{ANDROID_CANVAS}dp"
    android:height="{ANDROID_CANVAS}dp"
    android:viewportWidth="{ANDROID_CANVAS}"
    android:viewportHeight="{ANDROID_CANVAS}">
    <group
        android:pivotX="{cx}"
        android:pivotY="{cy}"
        android:scaleX="{scale:.6f}"
        android:scaleY="{scale:.6f}"
        android:translateX="{ANDROID_CANVAS / 2 - cx}"
        android:translateY="{ANDROID_CANVAS / 2 - cy}">
        <group>
            <clip-path android:pathData="{CLIP_PATH}" />
            <path
                android:fillColor="{letter}"
                android:fillType="evenOdd"
                android:pathData="{D_PATH}" />
        </group>
        <path
            android:fillColor="{check}"
            android:pathData="{CHECK_PATH}" />
    </group>
</vector>
"""


def write(path, text):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w") as f:
        f.write(text)


def android():
    write(os.path.join(ANDROID_RES, "drawable", "ic_launcher_foreground.xml"), vector_drawable(LETTER, CHECK))
    write(os.path.join(ANDROID_RES, "drawable", "ic_launcher_monochrome.xml"), vector_drawable("#FF000000", "#FF000000"))
    write(os.path.join(ANDROID_RES, "values", "ic_launcher_background.xml"), f"""<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="ic_launcher_background">{BACKGROUND}</color>
</resources>
""")
    adaptive = """<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/ic_launcher_background" />
    <foreground android:drawable="@drawable/ic_launcher_foreground" />
    <monochrome android:drawable="@drawable/ic_launcher_monochrome" />
</adaptive-icon>
"""
    write(os.path.join(ANDROID_RES, "mipmap-anydpi-v26", "ic_launcher.xml"), adaptive)
    write(os.path.join(ANDROID_RES, "mipmap-anydpi-v26", "ic_launcher_round.xml"), adaptive)

    # Legacy PNGs for API 24-25: the visible 72dp of the adaptive canvas.
    legacy_fraction = 50 / 72
    for density, size in {"mdpi": 48, "hdpi": 72, "xhdpi": 96, "xxhdpi": 144, "xxxhdpi": 192}.items():
        folder = os.path.join(ANDROID_RES, f"mipmap-{density}")
        os.makedirs(folder, exist_ok=True)
        render(svg(size, legacy_fraction, BACKGROUND, LETTER, CHECK, "rounded"), os.path.join(folder, "ic_launcher.png"), size)
        render(svg(size, legacy_fraction, BACKGROUND, LETTER, CHECK, "circle"), os.path.join(folder, "ic_launcher_round.png"), size)


def ios():
    os.makedirs(IOS_ICONSET, exist_ok=True)
    size, fraction = 1024, 0.5
    render(svg(size, fraction, BACKGROUND, LETTER, CHECK), os.path.join(IOS_ICONSET, "AppIcon.png"), size, opaque=True)
    render(svg(size, fraction, None, LETTER, CHECK), os.path.join(IOS_ICONSET, "AppIcon-Dark.png"), size)
    render(svg(size, fraction, "#000000", "#FFFFFF", "#B3B3B3"), os.path.join(IOS_ICONSET, "AppIcon-Tinted.png"), size)

    image = {"idiom": "universal", "platform": "ios", "size": "1024x1024"}
    contents = {
        "images": [
            {**image, "filename": "AppIcon.png"},
            {**image, "filename": "AppIcon-Dark.png", "appearances": [{"appearance": "luminosity", "value": "dark"}]},
            {**image, "filename": "AppIcon-Tinted.png", "appearances": [{"appearance": "luminosity", "value": "tinted"}]},
        ],
        "info": {"author": "xcode", "version": 1},
    }
    write(os.path.join(IOS_ICONSET, "Contents.json"), json.dumps(contents, indent=2) + "\n")
    write(os.path.join(os.path.dirname(IOS_ICONSET), "Contents.json"),
          json.dumps({"info": {"author": "xcode", "version": 1}}, indent=2) + "\n")


if __name__ == "__main__":
    android()
    ios()
