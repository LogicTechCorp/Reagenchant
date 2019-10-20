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
	echo Making %%x_helmet.json
	(
		echo {
		echo 	"parent": "item/generated",
		echo 	"textures": {
		echo 		"layer0": "item/%%x_helmet"
		echo 	},
		echo    "overrides": [
        echo        {
        echo            "predicate": {
        echo                "reagenchant:broken": 1.0
        echo            },
        echo            "model": "reagenchant:item/broken_%%x_helmet"
        echo        }
        echo    ]
		echo }
	) > %%x_helmet.json
)

for %%x in (%*) do (
	echo Making broken_%%x_helmet.json
	(
		echo {
		echo 	"parent": "item/generated",
		echo 	"textures": {
		echo 		"layer0": "reagenchant:item/broken_%%x_helmet"
		echo 	}
		echo }
	) > broken_%%x_helmet.json
)

for %%x in (%*) do (
	echo Making %%x_chestplate.json
	(
		echo {
		echo 	"parent": "item/generated",
		echo 	"textures": {
		echo 		"layer0": "item/%%x_chestplate"
		echo 	},
		echo    "overrides": [
        echo        {
        echo            "predicate": {
        echo                "reagenchant:broken": 1.0
        echo            },
        echo            "model": "reagenchant:item/broken_%%x_chestplate"
        echo        }
        echo    ]
		echo }
	) > %%x_chestplate.json
)

for %%x in (%*) do (
	echo Making broken_%%x_chestplate.json
	(
		echo {
		echo 	"parent": "item/generated",
		echo 	"textures": {
		echo 		"layer0": "reagenchant:item/broken_%%x_chestplate"
		echo 	}
		echo }
	) > broken_%%x_chestplate.json
)

for %%x in (%*) do (
	echo Making %%x_leggings.json
	(
		echo {
		echo 	"parent": "item/generated",
		echo 	"textures": {
		echo 		"layer0": "item/%%x_leggings"
		echo 	},
		echo    "overrides": [
        echo        {
        echo            "predicate": {
        echo                "reagenchant:broken": 1.0
        echo            },
        echo            "model": "reagenchant:item/broken_%%x_leggings"
        echo        }
        echo    ]
		echo }
	) > %%x_leggings.json
)

for %%x in (%*) do (
	echo Making broken_%%x_leggings.json
	(
		echo {
		echo 	"parent": "item/generated",
		echo 	"textures": {
		echo 		"layer0": "reagenchant:item/broken_%%x_leggings"
		echo 	}
		echo }
	) > broken_%%x_leggings.json
)

for %%x in (%*) do (
	echo Making %%x_boots.json
	(
		echo {
		echo 	"parent": "item/generated",
		echo 	"textures": {
		echo 		"layer0": "item/%%x_boots"
		echo 	},
		echo    "overrides": [
        echo        {
        echo            "predicate": {
        echo                "reagenchant:broken": 1.0
        echo            },
        echo            "model": "reagenchant:item/broken_%%x_boots"
        echo        }
        echo    ]
		echo }
	) > %%x_boots.json
)

for %%x in (%*) do (
	echo Making broken_%%x_boots.json
	(
		echo {
		echo 	"parent": "item/generated",
		echo 	"textures": {
		echo 		"layer0": "reagenchant:item/broken_%%x_boots"
		echo 	}
		echo }
	) > broken_%%x_boots.json
)