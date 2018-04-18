# Recipe created by recipetool
# This is the basis of a recipe and may need further editing in order to be fully functional.
# (Feel free to remove these comments when editing.)

SUMMARY = "A SocketCAN abstraction layer for NodeJS."
# WARNING: the following LICENSE and LIC_FILES_CHKSUM values are best guesses - it is
# your responsibility to verify that the values are complete and correct.
#
# The following license files were not able to be identified and are
# represented as "Unknown" below, you will need to check them yourself:
#   node_modules/xml2js/node_modules/sax/LICENSE
#   node_modules/nan/LICENSE.md
#
# NOTE: multiple licenses have been detected; they have been separated with &
# in the LICENSE value for now since it is a reasonable assumption that all
# of the licenses apply. If instead there is a choice between the multiple
# licenses then you should change the value to separate the licenses with |
# instead of &. If there is any doubt, check the accompanying documentation
# to determine which situation is applicable.
LICENSE = "MIT & Apache-2.0 & ISC"
LIC_FILES_CHKSUM = "file://docs/assets/vendor/prettify/COPYING;md5=3b83ef96387f14655fc854ddc3c6bd57 \
                    file://node_modules/xml2js/LICENSE;md5=d9fc599c8d9e6f8665063862aac66dfc \
                    file://node_modules/xml2js/node_modules/sax/LICENSE;md5=326d5674181c4bb210e424772c60fa80 \
                    file://node_modules/xml2js/node_modules/xmlbuilder/LICENSE;md5=eb65a7137aed46eac98ceb8cb8b057a1 \
                    file://node_modules/nan/LICENSE.md;md5=e6270b7774528599f2623a4aeb625829"

SRC_URI = "npm://registry.npmjs.org;name=socketcan;version=${PV}"

NPM_SHRINKWRAP := "${THISDIR}/${PN}/npm-shrinkwrap.json"
NPM_LOCKDOWN := "${THISDIR}/${PN}/lockdown.json"

inherit npm

# Must be set after inherit npm since that itself sets S
S = "${WORKDIR}/npmpkg"
LICENSE_${PN}-nan = "MIT"
LICENSE_${PN}-xml2js-sax = "ISC"
LICENSE_${PN}-xml2js-xmlbuilder = "MIT"
LICENSE_${PN}-xml2js = "MIT"
LICENSE_${PN} = "Apache-2.0"


