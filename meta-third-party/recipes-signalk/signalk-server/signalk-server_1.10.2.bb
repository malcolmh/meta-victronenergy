# Description: fetch & package signalk-server

# This recipe was handmade; rather then using the devtool and OE npm.class
# functionality; since they take forever with a project with many
# dependencies like this one.
#
# The cli to install nodejs projects, npm, supports cross-compiling.
# Implementation as below is quite similar to do_compile() from npm.class
# https://github.com/openembedded/openembedded-core/blob/master/meta/classes/npm.bbclass
#
# The best `documentation` that I could find is this one:
# https://github.com/nodejs/node-gyp/issues/829
#
# From OE its this one:
# https://wiki.yoctoproject.org/wiki/TipsAndTricks/NPM

# Note that this is just signalk-server license. Via dependencies it drags
# in a whole lot more types of licenses.
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://tmp/lib/node_modules/signalk-server/LICENSE;md5=34f8c1142fd6208a8be89399cb521df9"

inherit daemontools

SRC_URI = " \
	file://start-signalk.sh \
	file://settings.json \
	file://venus.json \
"

DEPENDS = " \
	nodejs-native \
"

RDEPENDS_${PN} += " \
	nodejs \
	nodejs-npm \
"

DAEMONTOOLS_SERVICE_DIR = "${sysconfdir}/${PN}/service"
DAEMONTOOLS_RUN = "${libdir}/node_modules/signalk-server/bin/start-signalk.sh"

# ${@npm_oe_arch_map(d.getVar('TARGET_ARCH'), d)}"

# TODO: fix this for us harmless hardcoding by using the, or a, map like in
# npm.class
NPM_ARCH ?= "arm" 

do_compile() {
	# Fetch & install signalk-server

	npm --arch=${NPM_ARCH} --target_arch=${NPM_ARCH} install -g --prefix ./tmp signalk-server@${PV}

        cd ./tmp/lib/node_modules/signalk-server

	# install plugins
	# TODO: this could perhaps be done better, as now we specify a version here,
	#       inside the recipe. Which is not common practice in OE.
	npm --arch=${NPM_ARCH} --target_arch=${NPM_ARCH} install signalk-venus-plugin@1.10.0
	npm --arch=${NPM_ARCH} --target_arch=${NPM_ARCH} install @signalk/signalk-node-red@2.8.0

	# remove the files in put/test: they are compiled, though not cross-compiled thus
	# giving QA errors as well as being useless; and also they are not necessary
	rm -rf ./node_modules/put/test
}

do_install() {
        install -d ${D}/${libdir}/node_modules

	# find ${WORKDIR}/${BP}/tmp/lib/node_modules/signalk-server/ -type f -exec 'install -m 0755 "{}" ${D}${libdir}/node_modules/signalk-server' \;
	# above gives a an error. (find: `....`: no such file or directory). do the cp equivalent instead
	cp -R --no-dereference --preserve=mode,links -v ${WORKDIR}/${BP}/tmp/lib/node_modules/signalk-server ${D}${libdir}/node_modules/

	INSTALLDIR=${D}${libdir}/node_modules/signalk-server

	install -m 0755 ${WORKDIR}/start-signalk.sh ${INSTALLDIR}/bin

	# this folder keeps the default settings. start-signalk.sh copies them
	# to the data partition on first boot.
	install -d ${INSTALLDIR}/defaults
	install -m 0755 ${WORKDIR}/settings.json ${INSTALLDIR}/defaults
	install -m 0755 ${WORKDIR}/venus.json ${INSTALLDIR}/defaults
}

FILES_${PN} += "${libdir}/node_modules/signalk-server"
