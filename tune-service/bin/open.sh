#!/system/bin/sh
# NEMESIS Manager > Packages > Launch (and `nemesis pkg-launch paytondev.launchpad`):
# open the same component the TUNE hold opens.
. /data/nemesis/apps/paytondev.launchpad/scripts/common.sh
TARGET_COMPONENT=com.paytondev.launchpad/.MainActivity
[ -r "$HERE/knob.conf" ] && . "$HERE/knob.conf"
am start -n "$TARGET_COMPONENT" -f 0x10000000
