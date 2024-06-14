![Compost Bag]
![Requires Forge]![Versions]

# Updating to 1.20.5
On updating to 1.20.5, all the NBT data that compost bags originally had in old worlds will be stored as a `custom_data` component.
This data is no longer used by Compost Bag in favor of new data components `compostbag:max_bonemeal_count`,
`compostbag:bonemeal_count`, `compostbag:max_compost_level`, and `compostbag:compost_level`. To fix old bags, the world must be opened with an NBT
editor and manually converted to use the new components. Unfortunately, there is no way to perform these conversions of 
data in the mod. Another change to note, configs no longer exist in favor of the `compostbag:max_bonemeal_count`
component. On the GitHub page for the mod, there is a folder `ExtraDatapacks` which contains a default datapack to
double the max size. Simply copy this datapack and change the max bonemeal count to your desired amount and enable the 
datapack. This also allows creators to add more recipes for custom compost bags of varying capacity and varying fill
levels, if they so desire.

---

The compost bag is a utility item that allows you to compost items on the go, without needing to place down any pesky blocks!
# In GUI
While in a GUI, the bag can be right-clicked to compost an item, input bonemeal, or remove bonemeal. Holding down right-click will speed up the process! You can also carry the bag with the mouse and right-click on slots to compost many items at once, pick up bonemeal, or remove bonemeal.
# In World
In the world, the item can be used just like bonemeal to grow crops and saplings, grass and other flora. It also works in dispensers.

# TODO
- Transfer api support for the bag? Not really sure of the details of that api, so might not be suitable.

[Compost Bag]: https://raw.githubusercontent.com/dhyces/CompostBag/info/marketing/compost_bag_header.png "Compost Bag"
[Requires Forge]: https://img.shields.io/badge/Loader-NeoForge%2C%20Fabric-a8320c?style=for-the-badge "Requires Forge"
[Versions]: https://img.shields.io/badge/Versions-1.18.X%2C%201.19.X%2C%201.20.X%2C%201.21-a8320c?style=for-the-badge "1.18.X, 1.19.X, 1.20.X, 1.21"