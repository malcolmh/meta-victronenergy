DESCRIPTION = "Creates the config files which are used runtime by Venus"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

inherit www

PACKAGE_ARCH = "${MACHINE_ARCH}"

SRC_URI += " \
	file://board-compat \
	file://get-unique-id \
	file://hw-revision \
	file://installation-name \
	file://product-id \
	file://product-name \
	file://machine-conf.sh \
"
SRC_URI_append_ccgx += "file://get-unique-id.c"
SRC_URI_append_sunxi += "file://canbus_ports.in"

inherit update-rc.d

RDEPENDS_${PN} += "bash"

INITSCRIPT_NAME = "machine-conf.sh"
INITSCRIPT_PARAMS = "start 90 S ."

do_compile () {
	if [ -f ${WORKDIR}/get-unique-id.c ]; then
		${CC} ${CFLAGS} ${LDFLAGS} \
			${WORKDIR}/get-unique-id.c -o ${WORKDIR}/get-unique-id
	fi
}

do_install_append() {
	conf=${D}${sysconfdir}/venus
	mkdir -p $conf

	echo ${MACHINE} > $conf/machine

	if [ -n "${@bb.utils.contains("MACHINE_FEATURES", "headless", "1", "", d)}" ]; then touch $conf/headless; fi

	# mk2/mk3 port. Used by vecan-dbus aka mk2-dbus, as well as mk2vsc and some more
	if [ -n "${VE_MKX_PORT}" ]; then echo ${VE_MKX_PORT} > $conf/mkx_port; fi

	# vedirect ports. Used by serialstarter script
	if [ -n "${VE_VEDIRECT_PORTS}" ]; then echo ${VE_VEDIRECT_PORTS} > $conf/vedirect_ports; fi

	# vedirect port which is also used as console port. Like on ccgx/bbp3. Used
	# by the serial starter script.
	if [ -n "${VE_VEDIRECT_AND_CONSOLE_PORT}" ]; then echo ${VE_VEDIRECT_AND_CONSOLE_PORT} >  $conf/vedirect_and_console_port; fi

	if [ -n "${VE_BUZZER}" ]; then echo ${VE_BUZZER} > $conf/buzzer; fi

	if [ -n "${VE_PWM_BUZZER}" ]; then echo ${VE_PWM_BUZZER} > $conf/pwm_buzzer; fi

	# device for controlling the backlight
	if [ -n "${VE_BLANK_DISPLAY}" ]; then echo ${VE_BLANK_DISPLAY} > $conf/blank_display_device; fi
	if [ -n "${VE_BACKLIGHT}" ]; then echo ${VE_BACKLIGHT} > $conf/backlight_device; fi

	if [ -e ${WORKDIR}/canbus_ports.in ]; then
		install -m 644 ${WORKDIR}/canbus_ports.in ${conf}
	elif [ -n "${VE_CAN_PORTS}" ]; then
		echo ${VE_CAN_PORTS} > $conf/canbus_ports
	fi

	install -d ${D}/${base_sbindir}
	install -m 755 ${WORKDIR}/get-unique-id ${D}/${base_sbindir}

	install -d ${D}/${bindir}
	install -m 755 ${WORKDIR}/board-compat ${D}/${bindir}
	install -m 755 ${WORKDIR}/hw-revision ${D}/${bindir}
	install -m 755 ${WORKDIR}/installation-name ${D}/${bindir}
	install -m 755 ${WORKDIR}/product-id ${D}/${bindir}
	install -m 755 ${WORKDIR}/product-name ${D}/${bindir}

	install -d ${D}${WWW_ROOT}/venus
	ln -s /data/venus/unique-id ${D}${WWW_ROOT}/venus
	ln -s /opt/victronenergy/version ${D}${WWW_ROOT}/venus

	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${WORKDIR}/machine-conf.sh ${D}${sysconfdir}/init.d
}
