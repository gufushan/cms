# Function

## Definition
Relationship between independent variable and dependent variable

## Category:
* **pure function**, all independent variable in parameters
* **non-pure function**, at least one independent variable in closure scope


* **normal function** relation is complete, it can directly link from independent variable to dependent variable
* **high-order function** - relation is incomplete, missing part need to be passed in 
  before link from independent variable to dependent variable  
  E.g. (filter pred-func problem)

## Details
Function Application will generate value of dependent variable  
Function Name is not part of Function, it's a Reference to Function  
Function can not contains itself, but can contains a reference of itself  

Parameter order best practice:

* parameter for missing process comes first
* variadic parameter comes last

