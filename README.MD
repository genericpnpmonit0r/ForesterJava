# ForesterJava
The Minecraft forester script ported to Java

# Setup (Eclipse)
1. Open your MCP workspace (Beta 1.7.3 was used in this but other versions might work too.)
2. Open the Client project properties (Client > Right Click > Properties)
3. Go to Java Build Path > Source
4. Click on Link Source > Browse
5. Select the `sources` folder from the repository you cloned
6. Click Next > Finish
(Optionally repeat steps 3 to 6 if adding the `external` source folder.)
7. Open net.minecraft.src.World and add `forester.MCLevel` interface to the list of implementations

8. to be able to reobf with these classes you would need to copy the contents of sources/ and or external/ to src/minecraft/

See `example.java` for details on basic instructions how to use the code
See `external/forester/extra/WorldGeneratorShim.java` for an example on adapting the tree gen to work with MCP decomps.
The shim class mentioned above is optional (hence why it is in the `external` source folder) and you don't have to use it if you don't need to.
