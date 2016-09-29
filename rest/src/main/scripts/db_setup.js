/** Add indexes **/
db.users.ensureIndex( { "name": 1 }, { unique: true, sparse: true } );
db.users.ensureIndex( { "email": 1 }, { unique: true, sparse: true } );
db.caterers.ensureIndex( { "email": 1 }, { unique: true, sparse: true } );
db.caterers.ensureIndex( { "name": 1 }, { unique: true, sparse: true } );
db.caterers.ensureIndex( { 'location.geo' : "2dsphere" } );
db.dishes.ensureIndex( { 'caterer.location.geo' : "2dsphere" } );
db.dishes.ensureIndex( { "caterer._id" : 1 }, { unique: false, sparse: true } );
db.orders.createIndex( { "readableId": 1 }, { unique: true, sparse: true } );
db.vouchers.createIndex( { "code": 1 }, { unique: true, sparse: true } );

/** Insert caterers **/
db.caterers.insert(
  [{
    "name": "Super Thai",
    "description": "Super Thai - Noodles, Curry dishes",
    "manager": "John Lee",
    "email": "jlee@superthai.com",
    "phone": "312-211-8911",
    "location" : {
      "address" : {
        "label" : "House next to the police station",
        "houseNumber" : "832",
        "street" : "W. Wrightwood Avenue",
        "city" : "Chicago",
        "postalCode" : NumberInt(60614),
        "state" : "Illinois",
        "country" : "USA"
      },
      "geo" : {
        "type" : "Point",
        "coordinates" : [-87.6502373, 41.9282773]
      }
    },
    "workingTimes" : {
      "hours" : {
          "mon" : [{"open" : 492,"close" : 868}, 
									{"open" : 1074,"close" : 1395}],
          "tue" : [ {"open" : 517,"close" : 831}, 
									{"open" : 1059,"close" : 1433}],
          "wed" : [{"open" : 428,"close" : 711}, 
									{"open" : 1052,"close" : 1397}],
          "thu" : [{"open" : 529,"close" : 889},
									{"open" : 1034,"close" : 1349}],
          "fri" : [{"open" : 449,"close" : 810}, 
									{"open" : 1076,"close" : 1373}],
          "sat" : [{"open" : 448, "close" : 815},
									{"open" : 1055,"close" : 1345}],
          "sun" : [{"open" : 494,"close" : 880},
									{"open" : 1065,"close" : 1356}]
      },
      "minimumPreparationTime" : 30
  },
    "createdAt" : NumberLong(1424556053008),
    "updatedAt" : NumberLong(1424556053008)
  },
  {
    "name": "Super Kebab",
    "description": "Super Kebab - Kebabs and Middle Eastern Food",
    "manager": "Amad El Azuz",
    "email": "aeazuz@superkebab.com",
    "phone": "312-211-8912",
    "location" : {
      "address" : {
        "label" : "House not far from the police station",
        "houseNumber" : "2622",
        "street" : "North Clark Street",
        "city" : "Chicago",
        "postalCode" : NumberInt(60614),
        "state" : "Illinois",
        "country" : "USA"
      },
      "geo" : {
        "type" : "Point",
        "coordinates" : [-87.643631, 41.930149]
      }
    },
    "workingTimes" : {
      "hours" : {
          "sat" : [{"open" : 448, "close" : 815},
									{"open" : 1055,"close" : 1345}],
          "sun" : [{"open" : 494,"close" : 880},
									{"open" : 1065,"close" : 1356}]
      },
        "minimumPreparationTime" : 30
    },  
    "createdAt" : NumberLong(1424556053009),
    "updatedAt" : NumberLong(1424556053009)
  },
  {
    "name": "Fish & Chips",
    "description": "The bast Fish & Chips down under",
    "manager": "Dean Prob",
    "email": "dprob@fishchips.com",
    "phone": "312-211-8913",
    "location" : {
      "address" : {
        "label" : "Downtown area",
        "houseNumber" : "1",
        "street" : "Elizabeth Street",
        "city" : "Melbourne",
        "postalCode" : NumberInt(32901),
        "state" : "Victoria",
        "country" : "Australia"
      },
      "geo" : {
        "type" : "Point",
        "coordinates" : [144.963280, -37.814107]
      }
    },
    "workingTimes" : {
      "hours" : {
          "mon" : [{"open" : 492,"close" : 868}, 
									{"open" : 1074,"close" : 1395}],
          "tue" : [ {"open" : 517,"close" : 831}, 
									{"open" : 1059,"close" : 1433}],
          "wed" : [{"open" : 428,"close" : 711}, 
									{"open" : 1052,"close" : 1397}],
          "thu" : [{"open" : 529,"close" : 889},
									{"open" : 1034,"close" : 1349}],
          "fri" : [{"open" : 449,"close" : 810}, 
									{"open" : 1076,"close" : 1373}]
       },
        "minimumPreparationTime" : 30
    },
    "createdAt" : NumberLong(1424556053010),
    "updatedAt" : NumberLong(1424556053010)
  }
  ]
);

/** Insert User **/
db.users.insert(
  [{
	  "name":"Bill Gates",
	  "email":"bgates@microsoft.com",
	  "password":"hashed_password",
	  "allow": NumberInt(1),
	  "createdAt":NumberLong(1423427362620),
	  "updatedAt":NumberLong(1423427362620)
  },
  {
    "name":"Steve Jobs",
    "email":"sjobs@apple.com",
    "password":"hashed_password",
    "allow": NumberInt(1),
    "createdAt": NumberLong(1423427362621),
    "updatedAt": NumberLong(1423427362622)
  }]
);

/** Insert Dishes **/
db.dishes.insert(
  [{
	  "name": "Thai Inbox",
    "description": "Noodles with rice",
    "type": "main",
    "price": NumberInt(500),
    "cookingTime": NumberInt(5),
    "difficultyLevel": NumberInt(1),
    "caterer": 
      {
        "_id" : ObjectId("54e90015b634980ccd05e3bc"),
        "name": "Super Thai",
        "description": "Super Thai - Noodles, Curry dishes",
        "manager": "John Lee",
        "email": "jlee@superthai.com",
        "phone": "312-211-8911",
        "location" : {
          "address" : {
            "label" : "House next to the police station",
            "houseNumber" : "832",
            "street" : "W. Wrightwood Avenue",
            "city" : "Chicago",
            "postalCode" : NumberInt(60614),
            "state" : "Illinois",
            "country" : "USA"
          },
          "geo" : {
            "type" : "Point",
            "coordinates" : [-87.6502373, 41.9282773]
          }
        },
        "workingTimes" : {
          "hours" : {
              "mon" : [{"open" : 492,"close" : 868}, 
    									{"open" : 1074,"close" : 1395}],
              "tue" : [ {"open" : 517,"close" : 831}, 
    									{"open" : 1059,"close" : 1433}],
              "wed" : [{"open" : 428,"close" : 711}, 
    									{"open" : 1052,"close" : 1397}],
              "thu" : [{"open" : 529,"close" : 889},
    									{"open" : 1034,"close" : 1349}],
              "fri" : [{"open" : 449,"close" : 810}, 
    									{"open" : 1076,"close" : 1373}],
              "sat" : [{"open" : 448, "close" : 815},
    									{"open" : 1055,"close" : 1345}],
              "sun" : [{"open" : 494,"close" : 880},
    									{"open" : 1065,"close" : 1356}]
           },
           "minimumPreparationTime" : 30
        }, 
        "createdAt" : NumberLong(1424556053008),
        "updatedAt" : NumberLong(1424556053008)
      },
  	"ingredients": 
  		[{
  			"name": "Noodles",
  			"sequence": NumberInt(1),
  			"quantity": 1.0,
  			"measurementUnit": "G"
  		},
  		{
  			"name": "Rice",
  			"sequence": NumberInt(2),
  			"quantity": 1.0,
  			"measurementUnit": "G"
  		}],
  	"nutritionFacts": 
  		[{
  			"name": "Calories",
  			"value": 1250.0,
  			"unit": "KJ"
  		},
  		{
  			"name": "Proteins",
  			"value": 750.5,
  			"unit": "G"
  		}],
  		"videoUrl": "http://www.google.com",
  		"imageAfterUrl": "http://www.google.com",
  		"createdAt": NumberLong(1424042592185),
  		"updatedAt": NumberLong(1424042592185)
  	},
  	{
	  	"name": "Fish and Chips",
	  	"description": "Fresh fish and chips",
	  	"type": "main",
	  	"price": NumberInt(500),
	  	"cookingTime": NumberInt(5),
	  	"difficultyLevel": NumberInt(1),
	  	"caterer": 
        {
          "_id" : ObjectId("54e90015b634980ccd05e3be"),
          "name": "Fish & Chips",
          "description": "The bast Fish & Chips down under",
          "manager": "Dean Prob",
          "email": "dprob@fishchips.com",
          "phone": "312-211-8913",
          "location" : {
            "address" : {
              "label" : "Downtown area",
              "houseNumber" : "1",
              "street" : "Elizabeth Street",
              "city" : "Melbourne",
              "postalCode" : NumberInt(32901),
              "state" : "Victoria",
              "country" : "Australia"
            },
            "geo" : {
              "type" : "Point",
              "coordinates" : [144.963280, -37.814107]
            }
          },
          "workingTimes" : {
            "hours" : {
                "mon" : [{"open" : 492,"close" : 868}, 
      									{"open" : 1074,"close" : 1395}],
                "tue" : [ {"open" : 517,"close" : 831}, 
      									{"open" : 1059,"close" : 1433}],
                "wed" : [{"open" : 428,"close" : 711}, 
      									{"open" : 1052,"close" : 1397}],
                "thu" : [{"open" : 529,"close" : 889},
      									{"open" : 1034,"close" : 1349}],
                "fri" : [{"open" : 449,"close" : 810}, 
      									{"open" : 1076,"close" : 1373}]
             },
             "minimumPreparationTime" : 30
          }, 
          "createdAt" : NumberLong(1424042592185),
          "updatedAt" : NumberLong(1424042592185)
       },
    	"ingredients": 
    		[{
    			"name": "Fish",
    			"sequence": NumberInt(1),
    			"quantity": 1.0,
    			"measurementUnit": "G"
    		},
    		{
    			"name": "Chips",
    			"sequence": NumberInt(2),
    			"quantity": 1.0,
    			"measurementUnit": "G"
    		}],
    	"nutritionFacts": 
    		[{
    			"name": "Calories",
    			"value": 1250.0,
    			"unit": "KJ"
    		},
    		{
    			"name": "Proteins",
    			"value": 750.5,
    			"unit": "G"
    		}],
    		"videoUrl": "http://www.google.com",
    		"imageAfterUrl": "http://www.google.com",
    		"createdAt": NumberLong(1424042592185),
    		"updatedAt": NumberLong(1424042592185)
  	},{
	  	"name": "Noodles Curry",
	  	"description": "Fresh Noodles with curry",
    	"type": "main",
    	"price": NumberInt(500),
    	"cookingTime": NumberInt(5),
    	"difficultyLevel": NumberInt(1),
    	"caterer": 
      {
        "_id" : ObjectId("54e90015b634980ccd05e3be"),
        "name": "Fish & Chips",
        "description": "The bast Fish & Chips down under",
        "manager": "Dean Prob",
        "email": "dprob@fishchips.com",
        "phone": "312-211-8913",
        "location" : {
          "address" : {
            "label" : "Downtown area",
            "houseNumber" : "1",
            "street" : "Elizabeth Street",
            "city" : "Melbourne",
            "postalCode" : NumberInt(32901),
            "state" : "Victoria",
            "country" : "Australia"
          },
          "geo" : {
            "type" : "Point",
            "coordinates" : [144.963280, -37.814107]
          }
        },
        "workingTimes" : {
          "hours" : {
              "mon" : [{"open" : 492,"close" : 868}, 
    									{"open" : 1074,"close" : 1395}],
              "tue" : [ {"open" : 517,"close" : 831}, 
    									{"open" : 1059,"close" : 1433}],
              "wed" : [{"open" : 428,"close" : 711}, 
    									{"open" : 1052,"close" : 1397}],
              "thu" : [{"open" : 529,"close" : 889},
    									{"open" : 1034,"close" : 1349}],
              "fri" : [{"open" : 449,"close" : 810}, 
    									{"open" : 1076,"close" : 1373}]
           },
           "minimumPreparationTime" : 30
        }, 
        "createdAt" : NumberLong(1424042592185),
        "updatedAt" : NumberLong(1424042592185)
     },
  	"ingredients": 
  		[{
  			"name": "Fish",
  			"sequence": NumberInt(1),
  			"quantity": 1.0,
  			"measurementUnit": "G"
  		},
  		{
  			"name": "Chips",
  			"sequence": NumberInt(2),
  			"quantity": 1.0,
  			"measurementUnit": "G"
  		}],
  	"nutritionFacts": 
  		[{
  			"name": "Calories",
  			"value": 1250.0,
  			"unit": "KJ"
  		},
  		{
  			"name": "Proteins",
  			"value": 750.5,
  			"unit": "G"
  		}],
  		"videoUrl": "http://www.google.com",
  		"imageAfterUrl": "http://www.google.com",
  		"createdAt": NumberLong(1424042592185),
  		"updatedAt": NumberLong(1424042592185)
	}]
);

/** Insert Orders **/
 db.orders.insert( 
{
		"readableId" : "60D",
    "description" : "A new order",
    "amount" : NumberInt(500),
    "currency" : "AUD",
    "status" : "PENDING",
    "paid" : false,
    "dish" : {
      "_id" : "553292c04c399b1733997513",
	  	"name": "Thai Inbox",
      "description": "Noodles with rice",
      "type": "main",
      "price": NumberInt(500),
      "cookingTime": NumberInt(5),
      "difficultyLevel": NumberInt(1),
      "caterer": {
          "_id" : "55b060f077382594cd38373b",
          "name": "Super Thai",
          "description": "Super Thai - Noodles, Curry dishes",
          "manager": "John Lee",
          "email": "jlee@superthai.com",
          "phone": "312-211-8911",
          "location" : {
            "address" : {
              "label" : "House next to the police station",
              "houseNumber" : "832",
              "street" : "W. Wrightwood Avenue",
              "city" : "Chicago",
              "postalCode" : NumberInt(60614),
              "state" : "Illinois",
              "country" : "USA"
            },
            "geo" : {
              "type" : "Point",
              "coordinates" : [-87.6502373, 41.9282773]
            }
          },
          "workingTimes" : {
            "hours" : {
                "mon" : [{"open" : 492,"close" : 868}, 
      									{"open" : 1074,"close" : 1395}],
                "tue" : [ {"open" : 517,"close" : 831}, 
      									{"open" : 1059,"close" : 1433}],
                "wed" : [{"open" : 428,"close" : 711}, 
      									{"open" : 1052,"close" : 1397}],
                "thu" : [{"open" : 529,"close" : 889},
      									{"open" : 1034,"close" : 1349}],
                "fri" : [{"open" : 449,"close" : 810}, 
      									{"open" : 1076,"close" : 1373}],
                "sat" : [{"open" : 448, "close" : 815},
      									{"open" : 1055,"close" : 1345}],
                "sun" : [{"open" : 494,"close" : 880},
      									{"open" : 1065,"close" : 1356}]
             },
             "minimumPreparationTime" : 30
          	}, 
          "createdAt" : NumberLong(1424556053008),
          "updatedAt" : NumberLong(1424556053008)
        },
    	"ingredients": 
    		[{
    			"name": "Noodles",
    			"sequence": NumberInt(1),
    			"quantity": 1.0,
    			"measurementUnit": "G"
    		},
    		{
    			"name": "Rice",
    			"sequence": NumberInt(2),
    			"quantity": 1.0,
    			"measurementUnit": "G"
    		}],
    	"nutritionFacts": 
    		[{
    			"name": "Calories",
    			"value": 1250.0,
    			"unit": "KJ"
    		},
    		{
    			"name": "Proteins",
    			"value": 750.5,
    			"unit": "G"
    		}],
    	"videoUrl": "http://www.google.com",
    	"imageAfterUrl": "http://www.google.com",
    	"createdAt": NumberLong(1424042592185),
    	"updatedAt": NumberLong(1424042592185)
    },
    "createdBy" : "553292bd4c399700629d6f7c",
    "createdAt" : NumberLong(1429377728692),
    "updatedAt" : NumberLong(1429377728692)
});