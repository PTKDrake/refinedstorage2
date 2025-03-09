## [Unreleased]

### Changed

- The External Storage now supports multiple resource types on a single connected inventory.

### Fixed

- Fixed cables broken with a Wrench not stacking with newly crafted cables.
- Fixed slow performance in the Grid when searching.
- Fixed storages failing to load after removing an addon that adds more storage types.
- Fixed crash when trying to open block in spectator mode.
- Fixed crash when trying to open Grid with EnderIO autocrafting pattern in network.
- Fixed External Storage making resources flicker when the connected inventory is causing neighbor updates.

## [2.0.0-milestone.4.14] - 2025-02-23

### Added

- Autocrafting Upgrade

### Changed

- The filter slots for the Exporter, Constructor and Interface now display whether a resource is missing, the destination does not accept it, the resource cannot be autocrafted due to missing resources, or whether the resource is currently autocrafting.
- 'B' is now displayed after fluid amounts, indicating the amount in buckets.
- The Autocrafter now only connects to other autocrafters through its front face. The reason for this is so that you can connect it to an Interface and accept multi-type autocrafting inputs without the Interface being connected to your network.
- When using Autocrafter chaining, you can now only configure the locking mode on the head of the chain.
- For now, it is not possible to rotate the Relay. This behavior will be restored later.

### Fixed

- Fixed cables broken with a Wrench not stacking with any other cables.
- Fixed colored importers, exporters, and external storages not dropping when broken.
- Fixed fluid amounts displaying to 0.1 buckets and not 0.001. The suffix "m" is used.
- Fixed Pattern Grid processing matrix input slots not mentioning "SHIFT click to clear".
- Fixed amount buttons not working in the Autocrafting Preview when the amount is "0" after clicking the "Max" button.
- Fixed not being able to insert resources in a Grid when clicking on something that is autocraftable.
- Fixed not being able to specify amount &lt; 1 in filter and pattern creation slots.
- Fixed visual bug where fluid containers were being filled when hovering over a fluid in the Grid.
- Fixed clear button in the Pattern Grid for the smithing table not working.
- Fixed item count rendering behind the item in the Interface slots.
- Fixed pickaxes not influencing break speed of blocks.
- Fixed non-autocraftables Grid view filter still showing autocraftable resources as they get inserted.
- Fixed filter items from the Pattern Grid crafting matrix being dropped when the Pattern Grid is broken.
- Fixed created pattern not being insertable in an Autocrafter when it is dropped after breaking the Pattern Grid.
- Fixed autocrafting tasks being completed when not all processing outputs have been received yet.
- Fixed items not stacking when externally interacting with an Interface.
- Fixed Constructor in default scheduling mode not trying other filters when resource is not available.
- Fixed autocrafter chaining returning "Machine not found" when starting autocrafting.
- Fixed Regulator Upgrade not being allowed 4 times in the Importer or Exporter.

## [2.0.0-milestone.4.13] - 2025-02-01

### Added

- Colored variants are now moved to a separate creative mode tab.
- You can now start autocrafting tasks in the Relay's output network, using patterns and autocrafters from the input network.

### Fixed

- Fixed Crafter and Security Manager bottom sides being lit on NeoForge.
- Fixed pattern input slot in the Pattern Grid not being accessible as an external inventory.
- Fixed duplication bug with the Crafting Grid matrix and insert-only storages.
- Fixed not being able to deselect the Grid search box.
- Fixed Storage Block not showing the amount and capacity when inactive.

## [2.0.0-milestone.4.12] - 2025-01-27

### Added

- Autocrafting engine.
- The crafting preview now has the ability to fill out the maximum amount of a resource you can currently craft.
- In the crafting preview, you can now indicate whether you want to be notified when an autocrafting task is finished.

### Changed

- Autocrafting now handles multiple patterns with the same output correctly by trying to use the pattern with the
  highest priority first. If there are missing resources, lower priority patterns are checked.
- The Autocrafter now faces the block you're clicking when placing it, like the other cable blocks (like the Exporter or Importer).
- You can no longer cancel autocrafting tasks if there is not enough space in storage to return the intermediate task storage.
- The Autocrafting Monitor now shows the machine in which a resource is processing.

### Fixed

- Fixed amount in amount screens resetting when resizing the screen.

## [2.0.0-milestone.4.11] - 2024-12-08

### Added

- Ability to differentiate between insert and extract storage priorities. By default, the extract priority will match the insert priority unless configured otherwise.

### Fixed

- Fixed External Storage not connecting properly to fluid storages.
- Fixed Interface filter not respecting maximum stack size of a resource.
- Fixed potential crash when trying to build cable shapes.
- Fixed storage disk upgrade recipes not showing properly in recipe viewers.
- Protect against crashes from other mods when trying to build the cached Grid tooltip.
- Fixed charging energy items not working on Fabric.

## [2.0.0-milestone.4.10] - 2024-11-24

### Added

- Autocrafting Monitor
- Wireless Autocrafting Monitor
- Creative Wireless Autocrafting Monitor

### Changed

- The Autocrafting Monitor now has a sidebar with all tasks instead of using tabs.
- The auto-selected search box mode is now a global option used in the Autocrafter Manager as well.

### Removed

- Block of Quartz Enriched Iron (has been moved to addon mod)
- Block of Quartz Enriched Copper (has been moved to addon mod)

## [2.0.0-milestone.4.9] - 2024-11-01

### Added

- Autocrafter Manager
- You can now configure the view type of the Autocrafter Manager:
  - Visible (only show autocrafters that are configured to be visible to the Autocrafter Manager)
  - Not full (only show autocrafters that are not full yet)
  - All (show all autocrafters)

### Changed

- The search field in the Autocrafter Manager can now search in:
  - Pattern inputs
  - Pattern outputs
  - Autocrafter names
  - All of the above (by default)
- Due to technical limitations and the new filtering options listed above being client-side only, you can no longer shift-click patterns in the Autocrafter Manager.
- In the Autocrafter, you can now configure whether it is visible to the Autocrafter Manager (by default it's visible).

## [2.0.0-milestone.4.8] - 2024-10-12

### Added

- Autocrafter
  - Note: autocrafting itself hasn't been implemented yet. This is the in-game content, but not the autocrafting engine itself yet.
- The Relay now has support for propagating autocrafting when not in pass-through mode.

### Changed

- The Crafter has been renamed to "Autocrafter".
- Optimized memory usage and startup time of cable models. After updating, cables will appear disconnected, but this is only visual. Cause a block update to fix this.
- Optimized performance of searching in the Grid.
- Custom titles that overflow will now have a marquee effect instead, for every GUI.
- You can now define a priority in the Autocrafter.
- You can now change the name of a Autocrafter in the GUI.
- Changed "Crafter mode" to "Locking mode" with following options:
  - Never
  - Lock until redstone pulse is received
  - Lock until connected machine is empty (new, facilitates easier "blocking mode" without redstone)
  - Lock until all outputs are received (new, facilitates easier "blocking mode" without redstone)
  - Lock until low redstone signal
  - Lock until high redstone signal
- Resources in the Grid that are autocraftable now display an orange backdrop and tooltip to indicate whether the resource is autocraftable at a glance.
- Slots used in the Pattern Grid for pattern encoding and Crafting Grid crafting matrix slots now display an orange backdrop and tooltip to indicate whether the item is autocraftable at a glance. This checks patterns from your network and from your inventory.
- Added help tooltip for filtering based on recipe items in the Crafting Grid.
- The crafting amount and crafting preview screens have been merged. Changing the amount will update the live preview.
- The numbers on the crafting preview screen are now compacted with units.
- When requesting autocrafting multiple resources at once, which can happen via a recipe mod, all the crafting requests are now listed on the side of the GUI.
- You can now request autocrafting from the Storage Monitor if the resource count reaches zero.

### Fixed

- Fixed mouse keybindings not working on NeoForge.
- Fixed upgrade destinations not being shown on upgrades.
- Fixed resources with changed data format or ID causing entire storage to fail to load.
- Fixed crash when trying to export fluids from an External Storage on Fabric.
- The Configuration Card can now also transfer the (configured) Regulator Upgrade.

## [2.0.0-milestone.4.7] - 2024-08-11

### Added

- You can now upgrade Storage Disks and Storage Blocks to a higher tier by combining with a higher tier Storage Part. The original Storage Part will be returned.

### Changed

- Updated to Minecraft 1.21.1.
- The Network Transmitter and Wireless Transmitter GUI now has an inactive and active GUI animation.
- The Wireless Transmitter now shows whether it's inactive in GUI instead of always showing the range.

### Fixed

- Use new slimeballs convention tag for Processor Binding.
- Portable Grid search bar texture being positioned in the wrong way.
- External Storage screen unnecessarily showing upgrade slots.
- Grid setting changes not persisting after restarting Minecraft.
- Fixed not being able to extract fluids from the Grid with an empty bucket or other empty fluid container.
- All blocks and items now correctly retain their custom name.

## [2.0.0-milestone.4.6] - 2024-08-08

### Added

- Pattern Grid
- Pattern

### Changed

- The Pattern now shows the recipe in the tooltip.
- When a Pattern is created for a recipe, the Pattern will have a different texture and name to differentiate between empty patterns.
- The Pattern Grid now has additional support for encoding stonecutter and smithing table recipes.
- The Pattern output is now always rendered in the Pattern Grid result slot.
- You can now search in the Pattern Grid alternatives screen.
- In the Pattern Grid alternatives screen, all resources belonging to a tag or no longer shown at once. You can expand or collapse them.
- The tag names in the Pattern Grid alternatives screen will now be translated.
- "Exact mode" in the Pattern Grid has been replaced with "Fuzzy mode" (inverse).

### Fixed

- Clicking on a scrollbar no longer makes a clicking sound.
- Incorrect and outdated (mentioning NBT tags) help explanations for fuzzy mode.
- Amount screen allowing more than the maximum for fluids.
- Potential text overflow in the Grid for localization with long "Grid" text.

## [2.0.0-milestone.4.5] - 2024-07-26

### Added

- Ability to extract fluids from the Interface using an empty bucket or other empty fluid container.
- Support for the NeoForge config screen.

### Fixed

- Fixed crash when trying to export fluids into an Interface on Fabric.
- Fixed Relay configuration not being correct on NeoForge.
- Fixed crash in logs when trying to quick craft an empty result slot in the Crafting Grid.
- Fixed recipes not using silicon tag and Refined Storage silicon not being tagged properly.

## [2.0.0-milestone.4.4] - 2024-07-10

## [2.0.0-milestone.4.3] - 2024-07-06

### Added

- Ability to open Portable Grid with a keybinding.

### Fixed

- Fixed Relay model not being able to load correctly.
- Fixed not being able to ghost drag resources from recipe viewers into filter slots on NeoForge.
- Fixed extra dark backgrounds due to drawing background on GUIs twice.
- Fixed Configuration Card not being able to transfer upgrades for the Wireless Transmitter.
- Fixed upgrade inventories not maintaining order after reloading. Upgrade inventories from the milestone 4.2 are
  incompatible and will be empty.
- Fixed Wireless Transmitter not dropping upgrades when breaking block.

## [2.0.0-milestone.4.2] - 2024-07-06

## [2.0.0-milestone.4.1] - 2024-07-05

### Fixed

- Fixed creative mode tab icon on NeoForge showing a durability bar.

## [2.0.0-milestone.4.0] - 2024-07-04

### Added

- Ported to Minecraft 1.21.
- More help information for items.
- Quartz Enriched Copper, used to craft cables.
- Block of Quartz Enriched Copper

### Changed

- The mod ID has been changed from "refinedstorage2" to "refinedstorage". Worlds that used milestone 3 on Minecraft
  1.20.4 are no longer compatible.
- Recipes now use common tag conventions from NeoForge and Fabric.

### Fixed

- Regulator Upgrade having wrong GUI title.
- Crafting Grid not dropping crafting matrix contents when broken.
- "+1" button on amount screen not doing anything.

## [2.0.0-milestone.3.14] - 2024-06-28

### Added

- Disk Interface (formerly known as the "Disk Manipulator").
- Item tag translations.

### Fixed

- Relay having no help tooltip.
- Fixed bug where adding more Speed Upgrades would actually slow down the device even more.
- Fixed missing textures for scheduling mode side button.

## [2.0.0-milestone.3.13] - 2024-06-16

## [2.0.0-milestone.3.12] - 2024-06-16

### Removed

- The Trinkets integration has been removed and will be moved to an addon mod.

## [2.0.0-milestone.3.11] - 2024-06-16

### Removed

- The Curios integration has been removed and will be moved to an addon mod.

## [2.0.0-milestone.3.10] - 2024-06-16

## [2.0.0-milestone.3.9] - 2024-06-09

### Fixed

- Side button tooltip rendering issue with ModernUI.

## [2.0.0-milestone.3.8] - 2024-06-08

### Removed

- The REI integration has been removed and will be moved to an addon mod.

## [2.0.0-milestone.3.7] - 2024-06-03

### Removed

- The JEI integration has been removed and will be moved to an addon mod.

## [2.0.0-milestone.3.6] - 2024-05-18

### Added

- Relay

### Changed

- The Detector, Network Receiver, Network Transmitter and Security Manager will now always connect regardless of color.
- The Relay now has a "pass-through" mode. By default, pass-through is on, which means that when the Relay is active,
  the network signal from the input network will be passed through as-is to the output side.
- When the "pass-through" mode on the Relay is off, the network signal from the input network will no longer be passed
  through as-is to the output side, but you can choose to pass the energy buffer, security settings or (specific)
  storage resources of the input network to the output network.
- When using the Relay when "pass-through" mode is off, and when passing all storage resources or specific storage
  resources, you can choose the filter mode, whether fuzzy mode is enabled, the access mode and the priority of the
  storage exposed to the output network.

### Fixed

- Double slot highlighting in the Grid.
- Improved data corruption protection for storages.

## [2.0.0-milestone.3.5] - 2024-04-04

### Added

- Security Card
- Fallback Security Card
- Security Manager

### Changed

- The permissions for a Security Card must be configured through the card itself, instead of via the Security Manager.
- The Security Card can be bound to other (currently online) players via its GUI.
- The binding of a Security Card can now be cleared.
- The Security Card tooltip and GUI now show whether the permission has been touched/changed in any way.
- As soon as a Security Manager is placed, the storage network will be locked down by default. Start adding Security
  Cards to allow or deny specific access to players.
- To not lock the entire network by default for players who do not have a matching Security Card, a Fallback Security
  Card can be used to configure this behavior.
- Smooth scrolling, screen size and max row stretch are no longer Grid-specific settings, but are now global settings.

### Fixed

- Wireless Grid name not being correct in the GUI.

## [2.0.0-milestone.3.4] - 2024-03-16

### Added

- Void excess mode to storages.

### Fixed

- Fixed losing disk when using Wrench dismantling on the Portable Grid.
- Fixed losing energy when using Wrench dismantling on the Portable Grid and the Controller.
- Fixed changing side buttons not working on Forge.
- Fixed External Storage not displaying empty allowlist warning.
- Fixed incrementing starting from 1 in amount screens not having an intended off-by-one.
- Fixed problems moving network devices with "Carry On" mod.
- Fixed escape key not working on auto-selected Grid search box.

## [2.0.0-milestone.3.3] - 2024-02-17

### Added

- Ported to Minecraft 1.20.4.
- Custom disk models. Fluid disks now have a different model.
- Portable Grid
- Chinese translation by [@Jiangsubei](https://github.com/Jiangsubei).

### Changed

- The Portable Grid now shows an energy bar in the UI.
- The energy bar on creative items now shows the infinity symbol instead of the whole amount.

### Fixed

- Fixed bug where Grid contents weren't synced properly when a network merge occurs.
- Fixed incompatibility crash with InvMove on Fabric.

## [2.0.0-milestone.3.2] - 2023-11-03

### Added

- Configuration Card. It copies device configurations and can transfer upgrades.
- Network Receiver
- Network Card
- Network Transmitter

### Changed

- The Network Transmitter now goes into an "errored" state if there is no connection (anymore) with the Network
  Receiver (due to chunk unloading for example).
- The Network Transmitter will actively try to reconnect with the Network Receiver if connection is lost.

### Fixed

- Inactive Wireless Transmitter model being emissive.
- Unneeded network graph updating after placing a network device.
- Cable blocks not updating connections properly when using wrench.

## [2.0.0-milestone.3.1] - 2023-10-30

### Added

- "Open Wireless Grid" keybinding.
- Curios integration on Forge.
- Trinkets integration on Fabric.
- Storage Monitor

### Changed

- You can now recharge the Controller in item form.

### Fixed

- Fixed a random Grid crash.

### Removed

- The `useEnergy` config option for the Wireless Grid. If you do not wish to use energy, use the
  Creative Wireless Grid.

## [2.0.0-milestone.3.0] - 2023-08-27

### Added

- Wireless Grid
- Creative Wireless Grid
- Wireless Transmitter
- Range Upgrade
- Creative Range Upgrade
- Fully charged Controller variants to the creative mode tab.

### Changed

- The Forge variant now targets NeoForge instead of Forge.
- You can now always open the Wireless Grid, even if there is no network bound or if the Wireless Grid is out of
  energy.

### Fixed

- Fixed inactive Grid slots still rendering resources.
- Fixed being able to interact with inactive Grid.
- Fixed nearly on/off Controller model not being rendered correctly on Forge.
- Fixed Controller energy tooltip not working.

## [2.0.0-milestone.2.14] - 2023-08-19

### Added

- Support for JEI/REI exclusion zones.
- Support for JEI/REI ghost ingredient dragging.

## [2.0.0-milestone.2.13] - 2023-08-18

### Changed

- The Interface now supports fluids.

### Fixed

- Fixed filter slot hints not being aware of the resource types that they can show in a slot.
- Fixed Exporter only exporting 1 mB per cycle on Forge.
- Fixed not being able to use any blocks on Fabric or Forge.
- Fixed External Storage crash on Fabric when a stack with zero amount is exposed.

## [2.0.0-milestone.2.12] - 2023-08-06

### Added

- Constructor
- Regulator Upgrade
- Filter slot hints that show which resource will be put in a filter slot and what the effect of the filter is on the
  device.
- Grid slot hints that show which resource will be inserted or extracted in a Grid.
- Help information to the side buttons by pressing SHIFT.
- Help information on items.
- A warning to the "filter mode" button on the storage screens if there is an allowlist with no configured filters.
- The "supported by" tooltip on upgrade items now shows the devices that accept the upgrade.

### Changed

- The Constructor crafting recipe now takes 2 diamonds instead of 2 redstone.
- You can now select a "Scheduling mode" in the Constructor: first available, round robin, random.
- The "applicable upgrades" tooltip on the upgrade slot tooltip now shows the upgrade items in item form.
- The Regulator Upgrade now works in an Importer as well. It will only keep importing until the configured amount is
  reached.
- The Regulator Upgrade now needs to be configured separately, by using the upgrade. It can no longer be configured in
  the device GUI itself.

### Fixed

- Fixed Grid voiding fluids if there was no space in inventory on Fabric.
- Fixed Grid dropping fluid buckets if there was no space in inventory on Forge.
- Fixed compatibility with custom tooltips in the Grid.
- Fixed bundle tooltip in the Grid.
- Fixed changes to access mode or fuzzy mode not being persisted.
- Fixed being able to put any item in the upgrade slots.

## [2.0.0-milestone.2.11] - 2023-07-04

### Added

- Ported to Minecraft 1.20.1.

### Fixed

- Fixed not firing block break event on Fabric for the Destructor.

## [2.0.0-milestone.2.10] - 2023-05-29

### Added

- Ported to Minecraft 1.19.4
- Destructor
- Fortune Upgrade (I, II and III)
- Silk Touch Upgrade

### Changed

- The Detector screen now is a proper amount screen by having increment/decrement buttons and scrollbar support.
- The amount in an amount screen is now colored red if the amount is invalid.
- The Destructor crafting recipe now takes 2 diamonds instead of 2 redstone.

### Fixed

- Fixed missing Speed Upgrade energy usage config on Forge.
- Fixed Grid screen not handling network changes properly.
- Fixed Grid scrollbar scrolling when using SHIFT or CTRL.
- Fixed wrong Controller tooltip.

### Removed

- Removed "Fuzzy mode" from the Destructor as the filter in the Destructor compares with the block anyway.

## [2.0.0-milestone.2.9] - 2023-03-31

### Fixed

- Fixed not being able to update filter slots on servers.

### Added

- Detector

### Changed

- Detectors can now be placed sideways or upside down.
- Detectors no longer detect all resources when unconfigured.
- Redstone updates by Detectors are now rate-limited to once per second.
- For fluids, the Detector now always accepts the amount in buckets.

## [2.0.0-milestone.2.8] - 2023-03-04

### Fixed

- Fixed Disk Drive having 9 slots instead of 8.
- Fixed slow world loading.

### Added

- The upgrade slots now show their supported upgrades.
- Different Cable colors. They only connect to same colored cables or the default cable.
- Colored variant of exporters, importers and external storages. They connect the same way as colored cables.
- Support for using the R/U keys in JEI and REI on Grid slots and filtering slots
- Crafting Grid.
- JEI and REI recipe transfer integration for the Crafting Grid.
- The crafting matrix in the Crafting Grid now has a button and keybinding to clear to the player inventory.
  The keybinding is only available on Forge.
- A config option to clear items from the Crafting Grid crafting matrix to the player or network inventory.
- Support for collapsable entries for REI.
- Pressing CTRL + SHIFT on the crafting result slot filters the Grid view based on the items in the crafting matrix.
  The reason for this is that you can quickly see how much you have left in the storage network.

### Changed

- The button to clear to the network inventory next to the crafting matrix in the Crafting Grid is now disabled if
  the Crafting Grid is inactive.
- The keybinding to clear the Crafting Grid matrix to the network inventory is only available on Forge.
- The JEI recipe transfer integration for the Crafting Grid now only supports regular crafting recipes.
- Decreased amount of logging to the info level. Now most logging happens on the debug level.

### Removed

- Removed amount of stacks and max stacks stored on item storage tooltips.

## [2.0.0-milestone.2.7] - 2023-01-31

### Added

- Added a "Storage channel" filter in the Grid that determines which resource type is shown. Defaults to "All".

### Changed

- Ported to Minecraft 1.19.3.
- The regular Grid now shows fluids as well.
- You can insert fluids in the Grid by right-clicking a fluid container in the Grid slots.
- You no longer have to explicitly select a resource type for the filter configuration slots. You can set a fluid
  by right-clicking a fluid container in the filter slots.
- You can no longer insert fluids into the Grid or filter slots straight from the player inventory slots, you have to
  insert the fluid while holding the fluid container.

### Removed

- Removed the Fluid Grid, which has been combined into the regular Grid.

## [2.0.0-milestone.2.6] - 2023-01-13

### Fixed

- Fixed missing recoloring recipes for Grid and Controller to default color.
- Fixed missing recoloring recipes for Fluid Grid.

## [2.0.0-milestone.2.5] - 2023-01-11

### Fixed

- Fixed IO loops caused by Interfaces stealing from each other.
- Fixed storages from an External Storage not reporting when a resource has last changed.

### Changed

- An Interface that is acting as External Storage can no longer extract or insert from other Interfaces (and itself)
  that are acting as External Storage.

## [2.0.0-milestone.2.4] - 2022-11-01

### Fixed

- Fixed missing AutoConfig config option translations on Fabric.
- Fixed Grid resource failing to insert if another resource with the same name but different NBT data already exists.
- Fixed Importer not dropping upgrades when broken.
- Fixed Disk Drive inventory not being available as external inventory on Forge.

### Added

- Exporter
- Interface
- External Storage

### Changed

- You can now select a "Scheduling mode" in the Exporter: first available, round robin, random.
- The Interface no longer has dedicated import slots. The imported items now go into the export slots.
- The Interface now imports items immediately.
- "Exact mode" has been replaced with "Fuzzy mode", which is off by default for performance.
- The External Storage no longer shows the amount of resources stored on the GUI.
- The External Storage now supports multiple resource types at the same time.
- The External Storage no longer checks for external changes every tick, but rather has a cooldown system.

## [2.0.0-milestone.2.3] - 2022-08-26

### Changed

- Ported to Minecraft 1.19.2.

### Fixed

- Fixed mixin crash on startup on Fabric.

### Added

- NoIndium mod is now packaged with the mod on Fabric to avoid launching Sodium without Indium.

## [2.0.0-milestone.2.2] - 2022-08-06

### Changed

- All directional blocks no longer transmit a network signal out of the direction.
- All directional blocks no longer accept a network signal from the facing direction.
- Upgrade items now state the applicable destinations in the tooltip.
- Upgrade items can now have a maximum of 1 type per upgrade inventory.
- You can now SHIFT + CLICK transfer resources in the filter slots again.

### Fixed

- Fixed network connection state not rebuilding after using Wrench on a directional block.
- Fixed Grid tooltip being too small in some cases and item durability not being rendered.

### Added

- Upgrade
- Speed Upgrade
- Stack Upgrade

## [2.0.0-milestone.2.1] - 2022-07-30

### Changed

- The Importer will now extract as much of 1 resource type as possible, according to the per tick transfer quota, at
  once for all the inventory slots.
- The Importer no longer transmits a network signal on the direction it's facing.
- The Importer can now import from the Disk Drive.
- The Importer no longer has a dedicated item/fluid mode. It will import what it's connected to, 1 resource type per
  tick is possible.
- Updated to the latest Forge version.
- Ported to Minecraft 1.19.1.

### Fixed

- Fixed Grid stack zeroing not working correctly when Auto-selected mode is on.
- Fixed transferring items into Grid with NBT tag on Forge not working correctly.

### Added

- Importer.
- Emissive rendering.

## [2.0.0-milestone.2.0] - 2022-07-05

### Changed

- Ported to Minecraft 1.19.

### Added

- Added JEI support to Fabric.
- Added REI support to Forge.

### Fixed

- Fixed resource filter container updates not arriving properly on Forge.

## [2.0.0-milestone.1.4] - 2022-06-22

### Added

- The Wrench now dismantles devices when crouching.
  - The Disk Drive in item form now supports rendering of disks that were dismantled.
  - In order to retain Controller energy, the Controller must now be dismantled.
  - All config and upgrades are transferred to the item.
- You can now use any Wrench from other mods in order to rotate or dismantle.
- Item and fluid storage blocks.
- Initial advancements.

### Fixed

- Fixed inventory contents of devices not retaining their original order when reloading a world.
- Fixed bug where (already opened) Grid doesn't update if a storage is removed.
- Fixed last modified info in the Grid not being persisted.
- Fixed removals in filter inventory not being saved properly.

### Changed

- Ported to Minecraft 1.18.2.
- Grid auto-selection and JEI/REI synchronization are now two different options.
- Grid display settings are now stored in the client configuration, no longer per-block.
- You now need to crouch with a dye in order to change the color of a device.
- Item storage capacities are now multiples of 1024 to make it more stack-size friendly.
- Storage tooltips now have colors.
- Storage tooltips now show percentage full.
- Item storage tooltips now show amount of stacks and max stacks stored.

### Removed

- Removed the Patchouli integration.

## [2.0.0-milestone.1.3] - 2022-02-12

### Added

- Forge support.

### Fixed

- Any block can be rotated now if the item tag matches `c:wrenches`.
