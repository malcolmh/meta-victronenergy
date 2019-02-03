SUMMARY = "Victron Venus D-Bus plugin for node-red"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d7725b8b5e691085738d564afb312302"

RDEPENDS_${PN} += "\
	node-red \
"

SRC_URI = "\
	git://github.com/victronenergy/node-red-contrib-victron.git;protocol=https;tag=${PV} \
"

SRC_URI[md5sum] = "de57d1c11f568f3bf2cb6044184dc4eb"
SRC_URI[sha256sum] = "3ee220e9aa915f20f57f2014ec6b93cec353da14250f6fbd8f86f5f38b2fdcd0"

# prevent npm.bbclass from stripping off the node- from our package name. Doing so leads to
# errors when building with a openembedded-core version that includes this commit:
# https://github.com/openembedded/openembedded-core/commit/6b417c7c3a38463c64756beae9817fa2a80fd09e
NPMPN = "node-red-contrib-victron"

inherit npm

# npm.bbclass assumes we've been using the npm fetcher. Ofcourse we didn't; its eternally slow.
S = "${WORKDIR}/git"

# Skip the arch QA check, since it trips over some precompiled binaries in the put npm module:
# https://github.com/substack/node-put/issues/5
# Removing those files in do_compile_append() doesn't work with the recent npm.bbclass; as it
# runs npm install again in do_install() - which is weird; but thats how it is.

INSANE_SKIP_${PN}-put += "arch"
