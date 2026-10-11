# V1 compatibility and verification

The current preconditioning profile targets `wp_daudioplus_neev23_us` on
Android 4.4/API 19. It validates these firmware files before enabling Start:

| File | SHA-256 |
| --- | --- |
| `/system/framework/automotive.odex` | `dd72e04bce0a2b4bda1506308ac21f91002c1436553b8156c345cc4313661338` |
| `/system/framework/automotive-service.odex` | `3cf40fb00854c63afa49abea1dad18a0afcbc0dd68b17de9ed7ea3f5aeab4222` |

It checks HEV service `com.hkmc.micom.hev` / descriptor
`com.hkmc.hev.IHEVService` and BCM service `com.hkmc.micom.bcm` / descriptor
`com.hkmc.bcm.IBCMService`. An incompatible or unreadable interface leaves the
feature unavailable. Do not remove these checks to support another firmware;
establish its command and feedback profile first.

## Command profile

The Binder argument mapping was derived from the inspected OEM framework and
native navigation implementation, using the public
[gen5w-utils decryption implementation](https://github.com/Helloyunho/gen5w-utils/blob/master/decrypt_navi.py).
Proprietary firmware binaries and disassembly are not included in this repository.
The upstream [CAN reference logs](https://github.com/tylerharvey/Ioniq5_CAN/tree/main/preconditioning_messages)
are corroborating evidence; their bytes are not sent as raw Binder arguments.

- Start: HEV transactions 65(9), 66(18), wait 10 ms, then 62(2,3).
- Maintain: refresh only 65(9), 66(18) every 5 seconds.
- Cancel: stop refreshing, then attempt 65(2047), 66(1023), 62(0,0).
- Station events have an OEM 120 ms reset; command sequences are serialized
  through a 130 ms window.
- The 9 km / 18 minute values are synthetic request metadata, not a heating
  countdown or a changed navigation destination.
- BCM transaction 109 reports the conditioning indicator: 0 Off, 1 Active,
  other values Unknown. The failure field is kept raw; enum meanings are not
  invented.

The app does not transmit raw CAN/UART, write the global conditioning
preference, or coordinate with native navigation in V1. Start requires the
existing enable setting, EV READY, and an Off indicator. The BMS retains control
of temperature, charge, and other operating constraints.

## Session behavior and verification limits

A foreground service owns a requested session after the activity closes. Stop
is available while waiting. Recovery cancels uncertain ownership and never
automatically starts conditioning. A successful Binder call or elapsed 60–120
seconds does not establish activation; only vehicle feedback can show Active.

The source includes unit tests for the request/feedback distinction, cancellation,
communication failures, recovery, navigation between app pages, favorites,
connection formatting, and TUNE event parsing/hold detection. Emulator tests
and static profile derivation do not establish real battery heating. At source
publication, actual parked-car activation and cancellation have not yet been
verified in this workspace.
