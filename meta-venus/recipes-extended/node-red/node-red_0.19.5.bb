DESCRIPTION = "Node-RED"
HOMEPAGE = "http://nodered.org"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d6f37569f5013072e9490d2194d10ae6"

DEPENDS = "nodejs-native"

RDEPENDS_${PN} += "\
	nodejs (>= 4.0.0) \
"

PR = "r0"

SRC_URI = "\
	https://github.com/${PN}/${PN}/releases/download/${PV}/${PN}-${PV}.zip \
"

SRC_URI[md5sum] = "a7c20a9708ea532a51376392a96d687b"
SRC_URI[sha256sum] = "5a6cfa7513d0dc40402bbed5ed7789b99279eec396761111d08a850a7c245eae"

S = "${WORKDIR}/${PN}-${PV}"

def get_nodejs_arch(target_arch, d):
    import re
    if re.match('p(pc|owerpc)(|64)', target_arch): return 'ppc'
    elif re.match('i.86$', target_arch): return 'ia32'
    elif re.match('x86_64$', target_arch): return 'x64'
    elif re.match('arm64$', target_arch): return 'arm'
    return target_arch

NODE_MODULES_DIR = "${prefix}/lib/node_modules/"
NPM_CACHE_DIR ?= "${WORKDIR}/npm_cache"
NPM_REGISTRY ?= "http://registry.npmjs.org/"
NPM_ARCH ?= "${@get_nodejs_arch(d.getVar('TARGET_ARCH'), d)}"
NPM_INSTALL_FLAGS ?= "--production --without-ssl --insecure --no-optional --verbose"

inherit daemontools

DAEMON_PN = "${PN}"
DAEMONTOOLS_SERVICE_DIR = "/etc/node-red/service"
DAEMONTOOLS_SCRIPT = "HOME=/home/root exec ${bindir}/node-red"
DAEMONTOOLS_LOG_DIR = "${DAEMONTOOLS_LOG_DIR_PREFIX}/node-red"

do_compile() {
	export HOME=${WORKDIR}

	# Set and clear cache
	npm set cache ${NPM_CACHE_DIR}
	npm cache clear --force

	# Install
	npm --registry=${NPM_REGISTRY} --arch=${NPM_ARCH} --target_arch=${NPM_ARCH} ${NPM_INSTALL_FLAGS} install
}

do_install() {
	export HOME=${WORKDIR}

	# Install
	install -d ${D}${NODE_MODULES_DIR}${PN}
	cp -r ${S}/* ${D}${NODE_MODULES_DIR}${PN}

	# Remove hardware specific files
	rm -rf ${D}${NODE_MODULES_DIR}${PN}/bin
	rm -rf ${D}${NODE_MODULES_DIR}${PN}/nodes/core/hardware

	# Set permissions
	chmod 0755 ${D}${NODE_MODULES_DIR}${PN}/red.js

	# Symlinks
	install -d ${D}${bindir}
	ln -s ${NODE_MODULES_DIR}${PN}/red.js ${D}${bindir}/${PN}
}

PACKAGES = "${PN}"

FILES_${PN} += "\
	${NODE_MODULES_DIR} \
	${bindir} \
"

INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
