ModsDotGroovy.make {
    def modid = this.buildProperties["mod_id"]
    def majorForgeVersion = (this.buildProperties["neo_version"] as String).split("\\.")[0]

    modLoader = "javafml"
    loaderVersion = "[${majorForgeVersion},)"

    license = "MIT"
    issueTrackerUrl = "https://github.com/dhyces/CompostBag/issues"

    mod {
        modId = modid
        displayName = this.buildProperties["mod_name"]
        version = this.version
        group = this.group
        authors = [this.buildProperties["mod_author"] as String]

        displayUrl = "https://www.curseforge.com/minecraft/mc-mods/compost-bag"
        sourcesUrl = "https://github.com/dhyces/CompostBag"
        logoFile = "logo.png"
        description = "Adds a bag that composts items like the composter and works like the bundle"

        onFabric {
            entrypoints {
                main = "dev.dhyces.compostbag.CompostBag"
                client = "dev.dhyces.compostbag.CompostBagClient"
            }
        }

        dependencies {
            onForge {
                minecraft = "${this.buildProperties["minecraft_version_range"]}"
                forge = "[${majorForgeVersion},)"
            }

            onFabric {
                minecraft = "${this.buildProperties["minecraft_version_range"]}"
                fabricloader = ">=${this.fabricLoaderVersion}"
                mod {
                    modId = 'fabric-api'
                    versionRange = '>=0.91.0'
                }
            }
        }
    }

    onFabric {
        environment = "*"
        mixin = [
                modid + ".mixins.json"
        ]
    }
}