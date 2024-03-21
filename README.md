![Compost Bag](https://github.com/dhyces/CompostBag/raw/info/marketing/compost_bag.png "Compost Bag")
![Requires Forge](https://img.shields.io/static/v1?style=for-the-badge&label=Loader&message=Forge&color=a8320c "Requires Forge")![1.18.X, 1.19.X, 1.20.X](https://img.shields.io/static/v1?style=for-the-badge&label=Versions&message=1.18.x&color=a8320c "1.18.X, 1.19.X, 1.20.X")

# Update to 1.20.5
On updating to 1.20.5, all of the NBT data that compost bags originally had will be stored as a `custom_data` component.
This data is no longer used by Compost Bag in favor of new data components `compostbag:max_bonemeal_count`,
`compostbag:bonemeal_count`, and `compostbag:compost_level`. To fix old bags, the world must be opened with an NBT
editor and manually converted to use the new components. Unfortunately, there is no way to perform these conversions of 
data in the mod. Another change to note, configs no longer exist in favor of the `compostbag:max_bonemeal_count`
component. On the GitHub page for the mod, there is a folder `ExtraDatapacks` which contains a default datapack to
double the max size. Simply copy this datapack and change the max bonemeal count to your desired amount and enable the 
datapack. This also allows creators to add more recipes for custom compost bags of varying capacity, if they so desire.

---

The compost bag is a utility item that allows you to compost items on the go, without needing to place down any pesky blocks!
# In GUI
While in a GUI, the bag can be right-clicked to compost an item, input bonemeal, or remove bonemeal. Holding down right-click will speed up the process! You can also carry the bag with the mouse and right-click on slots to compost many items at once, pick up bonemeal, or remove bonemeal.
# In World
In the world, the item can be used just like bonemeal to grow crops and saplings, grass and other flora. It also works in dispensers.