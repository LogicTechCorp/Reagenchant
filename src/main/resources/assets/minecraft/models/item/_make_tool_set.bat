@echo off
:: Vazkii's JSON creator for items
:: Put in your /resources/assets/%modid%/models/item
:: Makes basic item JSON files
:: Requires a _standard_item.json to be present for parenting
:: Can make multiple items at once
::
:: Usage:
:: _make (item name 1) (item name 2) (item name x)

setlocal enabledelayedexpansion

for %%x in (%*) do (
	echo Making %%x_sword.json
	(
		echo {
		echo 	"parent": "item/handheld",
		echo 	"textures": {
		echo 		"layer0": "item/%%x_sword"
		echo 	},
		echo    "overrides": [
        echo        {
        echo            "predicate": {
        echo                "reagenchant:broken": 1.0
        echo            },
        echo            "model": "reagenchant:item/broken_%%x_sword"
        echo        }
        echo    ]
		echo }
	) > %%x_sword.json
)

for %%x in (%*) do (
	echo Making broken_%%x_sword.json
	(
		echo {
		echo 	"parent": "item/handheld",
		echo 	"textures": {
		echo 		"layer0": "reagenchant:items/broken_%%x_sword"
		echo 	}
		echo }
	) > broken_%%x_sword.json
)

for %%x in (%*) do (
	echo Making %%x_pickaxe.json
	(
		echo {
		echo 	"parent": "item/handheld",
		echo 	"textures": {
		echo 		"layer0": "item/%%x_pickaxe"
		echo 	},
		echo    "overrides": [
        echo        {
        echo            "predicate": {
        echo                "reagenchant:broken": 1.0
        echo            },
        echo            "model": "reagenchant:item/broken_%%x_pickaxe"
        echo        }
        echo    ]
		echo }
	) > %%x_pickaxe.json
)

for %%x in (%*) do (
	echo Making broken_%%x_pickaxe.json
	(
		echo {
		echo 	"parent": "item/handheld",
		echo 	"textures": {
		echo 		"layer0": "reagenchant:items/broken_%%x_pickaxe"
		echo 	}
		echo }
	) > broken_%%x_pickaxe.json
)

for %%x in (%*) do (
	echo Making %%x_shovel.json
	(
		echo {
		echo 	"parent": "item/handheld",
		echo 	"textures": {
		echo 		"layer0": "item/%%x_shovel"
		echo 	},
		echo    "overrides": [
        echo        {
        echo            "predicate": {
        echo                "reagenchant:broken": 1.0
        echo            },
        echo            "model": "reagenchant:item/broken_%%x_shovel"
        echo        }
        echo    ]
		echo }
	) > %%x_shovel.json
)

for %%x in (%*) do (
	echo Making broken_%%x_shovel.json
	(
		echo {
		echo 	"parent": "item/handheld",
		echo 	"textures": {
		echo 		"layer0": "reagenchant:items/broken_%%x_shovel"
		echo 	}
		echo }
	) > broken_%%x_shovel.json
)

for %%x in (%*) do (
	echo Making %%x_axe.json
	(
		echo {
		echo 	"parent": "item/handheld",
		echo 	"textures": {
		echo 		"layer0": "item/%%x_axe"
		echo 	},
		echo    "overrides": [
        echo        {
        echo            "predicate": {
        echo                "reagenchant:broken": 1.0
        echo            },
        echo            "model": "reagenchant:item/broken_%%x_axe"
        echo        }
        echo    ]
		echo }
	) > %%x_axe.json
)

for %%x in (%*) do (
	echo Making broken_%%x_axe.json
	(
		echo {
		echo 	"parent": "item/handheld",
		echo 	"textures": {
		echo 		"layer0": "reagenchant:items/broken_%%x_axe"
		echo 	}
		echo }
	) > broken_%%x_axe.json
)

for %%x in (%*) do (
	echo Making %%x_hoe.json
	(
		echo {
		echo 	"parent": "item/handheld",
		echo 	"textures": {
		echo 		"layer0": "item/%%x_hoe"
		echo 	},
		echo    "overrides": [
        echo        {
        echo            "predicate": {
        echo                "reagenchant:broken": 1.0
        echo            },
        echo            "model": "reagenchant:item/broken_%%x_hoe"
        echo        }
        echo    ]
		echo }
	) > %%x_hoe.json
)

for %%x in (%*) do (
	echo Making broken_%%x_hoe.json
	(
		echo {
		echo 	"parent": "item/handheld",
		echo 	"textures": {
		echo 		"layer0": "reagenchant:items/broken_%%x_hoe"
		echo 	}
		echo }
	) > broken_%%x_hoe.json
)