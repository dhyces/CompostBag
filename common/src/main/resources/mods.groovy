MultiplatformModsDotGroovy.make {
    def modid = buildProperties["mod_id"]

    modLoader = "javafml"
    onNeoForge {
        loaderVersion = libs.versions.get("javafml_range")
    }

    license = "MIT"
    issueTrackerUrl = issueTrackerUrl = "https://github.com/dhyces/CompostBag/issues"

    mod {
        modId = modid
        displayName = buildProperties["mod_name"]
        authors = [(buildProperties["authors"] as String)]
        version = environmentInfo.version

        displayUrl = "https://www.curseforge.com/minecraft/mc-mods/compost-bag"
        logoFile = "assets/${modid}/logo.png"
        description = "Adds a bag that composts items like the composter and works like the bundle"
        sourcesUrl = "https://github.com/dhyces/CompostBag"

        onFabric {
            entrypoints {
                main = "dev.dhyces.compostbag.FabricCompostBag"
                client = "dev.dhyces.compostbag.FabricCompostBagClient"
            }
        }

        dependencies {
            onNeoForge {
                mod("neoforge") {
                    versionRange = "${libs.versions.get("neoforge_range")}"
                }
            }
            onFabric {
                minecraft = "${libs.versions.get("minecraft_range")}"
                fabricloader = ">=${libs.versions.get("fabric_loader")}"
                mod("fabric-api") {
                    versionRange = libs.versions.get("fabric_api_range")
                }
            }
        }
    }

//    onFabric {
//        if (Boolean.parseBoolean(buildProperties["at_enabled"] as String)) {
//            accessWidener = "${modid}.accesswidener"
//        }
//    }
    onNeoForge {
        accessTransformers {
            if (Boolean.parseBoolean(buildProperties["at_enabled"] as String)) {
                accessTransformer("META-INF/accessTransformer.cfg")
            }
        }
    }

    if (Boolean.parseBoolean(buildProperties["common_mixin_enabled"] as String)) {
        mixins {
            mixin("${modid}.mixins.json")
        }
    }

    onNeoForge {
        if (Boolean.parseBoolean(buildProperties["neoforge_mixin_enabled"] as String)) {
            mixins {
                mixin("${modid}.neo.mixins.json")
            }
        }
    }

    onFabric {
        environment = Environment.ANY
        if (Boolean.parseBoolean(buildProperties["fabric_mixin_enabled"] as String)) {
            mixins {
                mixin("${modid}.fabric.mixins.json")
            }
        }
    }
}