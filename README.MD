# ForesterJava
The Minecraft forester script ported to Java

# Setup (Eclipse)
1. Open your MCP workspace
2. Open the Client project properties (Client > Right Click > Properties)
3. Go to Java Build Path > Source
4. Click on Link Source > Browse
5. Select the `sources` folder from the repository you cloned
6. Click Next > Finish
7. Open net.minecraft.src.World and add `pnp.MCWorldAccessor` interface to the list of implementations
