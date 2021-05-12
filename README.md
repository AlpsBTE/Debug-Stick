# Debug-Stick
OUTDATED! DOES NOT WORK WITH MOHIST!

This is a fork of the [Debug-Stick mod](https://github.com/Tfarcenim/Debug-Stick) for Forge 1.12. It has a lighter functionality more similar to the vanilla debug-stick item. This mod can be installed on either the server (no client required) or the client for single player usage.

### Usage

To get the debug stick type: 
```
/give @s minecraft:stick 1 0 {display:{Name:"Debug Stick"},HideFlags:33,ench:[{id:33,lvl:1}],isDebugStick:1b}
```
What really matters is the `isDebugStick:1b` flag, the other tags are cosmetic.

Left click with the debug stick on any block to cycle through the properties.
Right click to cycle through all possible states of the selected property.
Sneak to cycle in reverse order.
