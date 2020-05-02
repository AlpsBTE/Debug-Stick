# Debug-Stick
This is a fork of hhe [Debug-Stick mod](https://github.com/Tfarcenim/Debug-Stick) for Forge 1.12. It has a lighter functionality more similat to the vanilla debug-stick item. This mod can be einstalled on either the server (no client required) or the client for single player usage.

### Usage

To get the debug stick type: 
```
/give @s minecraft:stick 1 0 {display:{Name:"Debug Stick"},HideFlags:33,ench:[{id:33,lvl:1}],isDebugStick:1b}
```
What really matters is the `isDebugStick:1b` flag, the other tags are cosmetic.

Right click with the debug stick on any block to cycle through all possible blockstates. If the player is sneaking, the cycling will happen in reverse order. 
