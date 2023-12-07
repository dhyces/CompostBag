ModsDotGroovy.make {
    def modid = this.buildProperties["mod_id"]

    modLoader = "javafml"
    loaderVersion = "${this.buildProperties["loader_version_range"]}"

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
        issueTrackerUrl = "https://github.com/dhyces/CompostBag/issues"
        logoFile = "logo.png"
        description = "Adds a bag that composts items like the composter and works like the bundle"

        onForge {
            updateJsonUrl = "https://github.com/dhyces/CompostBag/raw/info/update.json"
        }

        onFabric {
            entrypoints {
                main = "dev.dhyces.compostbag.CompostBag"
                client = "dev.dhyces.compostbag.CompostBagClient"
            }
        }

        dependencies {
            onForge {
                minecraft = "${this.buildProperties["minecraft_version_range"]}"
                neoforge = "${this.buildProperties["neo_version_range"]}"
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