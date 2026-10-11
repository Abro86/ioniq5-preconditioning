#!/usr/bin/env python3
"""Build NEMESIS service assets from source; never installs or connects to a car."""
import argparse
from pathlib import Path
import shutil
import subprocess
import zipfile

ROOT = Path(__file__).resolve().parents[1]

def pack(source, destination):
    with zipfile.ZipFile(destination, "w", zipfile.ZIP_DEFLATED) as archive:
        for path in sorted(source.rglob("*")):
            if path.is_file():
                archive.write(path, path.relative_to(source).as_posix())

def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--skip-build", action="store_true", help="Use the existing unsigned release APK")
    args = parser.parse_args()
    if not args.skip_build:
        subprocess.run([str(ROOT / "gradlew"), ":app:assembleRelease"], cwd=ROOT, check=True)
    apk = ROOT / "app/build/outputs/apk/release/app-release-unsigned.apk"
    if not apk.is_file():
        raise SystemExit("Build the release APK first.")
    # Work only in this repository's generated build directory.
    stage = ROOT / "build/tune-bundle"
    if stage.exists():
        shutil.rmtree(stage)
    package = stage / "package"
    shutil.copytree(ROOT / "tune-service", package)
    (package / "app").mkdir()
    shutil.copy2(apk, package / "app/Launchpad.apk")
    usb = stage / "usb"
    (usb / "packages").mkdir(parents=True)
    (usb / "NEMESIS.data").write_text("[nemesis]\nversion=1.0\ntype=install\npath=packages/paytondev.launchpad.zip\n")
    dist = ROOT / "dist"
    dist.mkdir(exist_ok=True)
    inner = usb / "packages/paytondev.launchpad.zip"
    pack(package, inner)
    shutil.copy2(inner, dist / inner.name)
    pack(usb, dist / "tune-knob-usb.zip")
    print(dist / "tune-knob-usb.zip")

if __name__ == "__main__":
    main()
