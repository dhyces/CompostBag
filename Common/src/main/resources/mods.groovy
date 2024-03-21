import modsdotgroovy.Dependency

ModsDotGroovy.make {
    def modid = this.buildProperties["mod_id"]

    modLoader = "javafml"
    loaderVersion = "${this.buildProperties["loader_version_range"]}"

    license = "MIT"
    issueTrackerUrl = "https://github.com/dhyces/CompostBag/issues"
    sourcesUrl = "https://github.com/dhyces/CompostBag"

    onForge {
        updateJsonUrl = "https://github.com/dhyces/CompostBag/raw/info/update.json"
    }

    mod {
        modId = modid
        displayName = this.buildProperties["mod_name"]
        version = this.version
        group = this.group
        authors = [this.buildProperties["mod_author"] as String]

        displayUrl = "https://www.curseforge.com/minecraft/mc-mods/compost-bag"
        logoFile = "logo.png"
        description = "Adds a bag that composts items like the composter and works like the bundle"

        onFabric {
            entrypoints {
                main = "dev.dhyces.compostbag.CompostBag"
                client = "dev.dhyces.compostbag.CompostBagClient"
            }
        }

        dependencies {
            onForge { // TODO: add this back
//                minecraft = "${this.buildProperties["minecraft_version_range"]}"
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

        onForge {
            dependencies = dependencies.collect { dep ->
                new Dependency() {
                    @Override
                    Map asForgeMap() {
                        def map = dep.asForgeMap()
                        def mand = map.get('mandatory')
                        map.remove('mandatory')
                        map.put('type', mand ? 'required' : 'optional')
                        return map
                    }
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