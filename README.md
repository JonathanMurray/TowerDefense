TowerDefense
============

A 2D-game written in Java using the Slick2D library (http://slick.ninjacave.com/).

The game is played by two people on the same computer. One person is responsible for constructing towers and purchasing items from the shop (using the mouse). The other person controls a hero (using the keyboard), that can be customized by choosing from different abilities. Together they have to defend their base from incoming waves of increasingly tough minion waves. 

-----

Stats for different entities in the game, such as enemies, abilities, items and towers, are specified in XML-files using a flexible format. Below is an example for the data specifying an AoE poison spell:

```xml
<type
        id="BREATH"
        name="Breath"
        tooltip="Foes in a square formation in front of hero are poisoned, taking @1 damage every @2 seconds. Lasts @3 seconds."
        cooldown="2.5"
        manaCost="50"
        icon="abilities/QuadSerpent.png"
    	sound="fireWater3.wav"
       	soundVolume="1.4">
        <links>
            <link>./action/effect/buff/effect/@amount</link>
            <link>./action/effect/buff/@cooldown</link>
            <link>./action/effect/buff/@duration</link>
        </links>
        <action
            type="AFFECT_TARGETS">
        	<target
        	    type="RECTANGLE_AHEAD"
        	    skipNFirstSquares="0"
        	    width="5"
        	    length="5"
                targetTeam="EVIL">
        	    <animation
        	        duration="90">
        	        <image>abilities/poison.png</image>
        	        <image>abilities/poison2.png</image>
        	    </animation>
        	</target>
        	<effect
        	    type="RECEIVE_BUFF">
        	    <buff
	        	    type="REPEAT_EFFECT_ON_SELF"
	        	    buffId="BREATH_DOT"
					cooldown="0.5"
					duration="7">
        	        <effect
        	            type="LOSE_HEALTH"
        	            amount="5"
        	            ignoreArmor="true"/>
        	        <animation
        	        	duration="90">
        	        	<image>abilities/poison.png</image>
        	        	<image>abilities/poison2.png</image>
        	    	</animation>
        	    </buff>   
        	</effect>
        </action>
			
    </type>
    ```
