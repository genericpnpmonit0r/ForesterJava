how to set up

open eclipse with whatever workspace you want to use,
open the client project (for example)
right click
click properties

go to java build path
go to the source tab
click link source
click browse
find the 'source' folder from the repo you cloned
click next, then click finish

then open your world class, then add replace 'implements IBlockAccess' with 'implements IBlockAccess, Trees.WorldAccessor'
and fix imports (ctrl-shift-o)

and if you have errors about not finding Block, go to Trees.java and fix imports

then you should be able to call Trees.generateTree(coords, height, world, Trees.Shape.round); for example
another example: Trees.generateTree((int)this.thePlayer.posX, (int)this.thePlayer.posY, (int)this.thePlayer.posZ, 10, theWorld, Trees.Shape.normal);

KNOWN ISSUES:
any subclasses of procedural tree will fail to work properly and will spawn leaves at 0,0,0
(because broken array position stuff?)
