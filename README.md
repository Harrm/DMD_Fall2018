#Specifications
A car sharing company owns a park of self-driving vehicles. The company rents out the vehicles to
customers who pay for the services.
Each customer is identified by a unique username and should have the following attributes:
* Full Name
* Username
* Email
* Location of residence (Country, City, Zip code)
* Phone Number

A company also runs a network of charging stations. There will be an option to find the nearest
charging station according to the location. Each charging station is identified by a UID and should
have the following attributes:
* GPS location
* Amount of available sockets
* Price or Cost of regarding charging amount
* Shape and Size of Plugs
* Time of charging

A company also runs workshops which repair cars. Workshops have providers of car parts which are
from other companies. Each workshop is identified by WID and should have the following attributes:

* Workshop location
* Car parts available according to the car type
* Availability of timing

There is also a provider of car parts who provides car parts to workshops. Each provider has unique
company id and should have the following attributes:

* Name of the provider
* Address of the provider
* Phone number of the provider
* Types of car part which the provider providesUse domain description (the output of Assignment #1) as the rest of the specification, including
interactions between customers and cars, customers and the company, cars and charging stations,
and workshops and providers.
