#!/system/bin/sh
# NEMESIS postinstall (root, cwd = package dir). Nothing to install: the knob daemon
# runs straight from the bundled dex and opens an already-registered app. svcmgr
# starts the knobd service automatically after this returns.
. /data/nemesis/apps/paytondev.launchpad/scripts/common.sh
log "postinstall: knobd will open TARGET_COMPONENT on a TUNE hold (no app install)"
exit 0
