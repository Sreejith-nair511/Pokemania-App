"""
Generate PNG launcher icons for all density buckets.
Design: deep space background + neon Pokeball.
"""
from PIL import Image, ImageDraw
import os, math

SIZES = {
    "mipmap-mdpi":    48,
    "mipmap-hdpi":    72,
    "mipmap-xhdpi":   96,
    "mipmap-xxhdpi":  144,
    "mipmap-xxxhdpi": 192,
}

def draw_icon(size):
    img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    s = size

    # ── Background circle ──────────────────────────────────────────
    # Dark space gradient approximated with concentric fills
    for i in range(s // 2, 0, -1):
        ratio = i / (s / 2)
        r = int(5  + 20 * (1 - ratio))
        g = int(10 + 15 * (1 - ratio))
        b = int(20 + 40 * (1 - ratio))
        d.ellipse([s//2 - i, s//2 - i, s//2 + i, s//2 + i], fill=(r, g, b, 255))

    cx, cy = s / 2, s / 2
    radius = s * 0.42

    # ── Pokeball top half (neon blue tint) ────────────────────────
    bbox = [cx - radius, cy - radius, cx + radius, cy + radius]
    d.pieslice(bbox, start=180, end=360, fill=(13, 31, 60, 255))

    # ── Pokeball bottom half (near black) ─────────────────────────
    d.pieslice(bbox, start=0, end=180, fill=(7, 14, 28, 255))

    # ── Outer ring ────────────────────────────────────────────────
    ring_w = max(2, int(s * 0.04))
    d.ellipse(bbox, outline=(0, 212, 255, 255), width=ring_w)

    # ── Horizontal divider ────────────────────────────────────────
    d.line(
        [cx - radius, cy, cx + radius, cy],
        fill=(0, 212, 255, 255),
        width=ring_w
    )

    # ── Centre button ─────────────────────────────────────────────
    btn_r = radius * 0.22
    btn_bbox = [cx - btn_r, cy - btn_r, cx + btn_r, cy + btn_r]
    # outer ring
    d.ellipse(btn_bbox, fill=(5, 10, 20, 255), outline=(0, 212, 255, 255), width=ring_w)
    # inner glow
    inner_r = btn_r * 0.55
    inner_bbox = [cx - inner_r, cy - inner_r, cx + inner_r, cy + inner_r]
    d.ellipse(inner_bbox, fill=(0, 212, 255, 255))

    # ── Star dots ─────────────────────────────────────────────────
    stars = [
        (0.15, 0.12), (0.75, 0.08), (0.88, 0.22),
        (0.08, 0.55), (0.92, 0.60), (0.20, 0.85),
        (0.80, 0.88), (0.50, 0.05),
    ]
    for sx, sy in stars:
        px, py = sx * s, sy * s
        sr = max(1, int(s * 0.015))
        d.ellipse([px-sr, py-sr, px+sr, py+sr], fill=(200, 230, 255, 160))

    return img

base = "app/src/main/res"
for folder, size in SIZES.items():
    path = os.path.join(base, folder)
    os.makedirs(path, exist_ok=True)
    icon = draw_icon(size)
    icon.save(os.path.join(path, "ic_launcher.png"))
    icon.save(os.path.join(path, "ic_launcher_round.png"))
    print(f"  {folder}: {size}x{size} px")

print("Done — all PNG icons generated.")
