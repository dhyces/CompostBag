MultiplatformModsDotGroovy.make {
    def modid = buildProperties["mod_id"]

    modLoader = "javafml"
    loaderVersion = "${buildProperties["loader_version_range"]}"

    license = "MIT"
    issueTrackerUrl = "https://github.com/dhyces/CompostBag/issues"
    sourcesUrl = "https://github.com/dhyces/CompostBag"

    mod {
        modId = modid
        displayName = buildProperties["mod_name"]
        version = buildProperties["version"]
        authors = [buildProperties["mod_author"] as String]

        displayUrl = "https://www.curseforge.com/minecraft/mc-mods/compost-bag"
        logoFile = "assets/compostbag/logo.png"
        description = "Adds a bag that composts items like the composter and works like the bundle"

        entrypoints {
            main "dev.dhyces.compostbag.FabricCompostBag"
            client "dev.dhyces.compostbag.FabricCompostBagClient"
        }

        dependencies {
            minecraft = "${buildProperties["minecraft_version_range"]}"
            onNeoForge {
                mod("neoforge") {
                    versionRange = "${buildProperties["neo_version_range"]}"
                }
            }

            onFabric {
                mod("fabricloader") {
                    versionRange = ">=${buildProperties["fabric_loader_version"]}"
                }
                mod("fabric-api") {
                    versionRange = ">=${(buildProperties["fabric_version"] as String).split("\\+")[0]}"
                }
            }
        }
    }

    onFabric {
        environment = "*"
        mixins {
            mixin("${modid}.mixins.json")
        }
    }

    onNeoForge {
        accessTransformers {
            accessTransformer("META-INF/accesstransformer.cfg")
        }
    }
}