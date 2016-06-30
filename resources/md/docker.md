# Docker

## Docker Engine
consists of:

* a daemon, a server process that manages all the containers
* a client, which acts as a remote control for the daemon
	
## Docker image
image file of Docker container

## Docker container
a process in a box  
The box contains everything the process might need, so it has the filesystem, system libraries, shell and such, but by default none of these are running. 


## Benefits:
Encapsulate artifact and runtime of program and its dependency, so that u can:

* isolate different program
* easily copy, move, remove installed program
* have uniform operate interface(startup, shutdown...)
            
        
## Command Example

### Odoo:  

  	docker run -d -e POSTGRES_USER=odoo -e POSTGRES_PASSWORD=odoo --name db postgres
	  docker run -p 8069:8069 --name odoo --link db:db -t odoo

### MySql:  

  	docker run --name some-mysql -e MYSQL_ROOT_PASSWORD=my-secret-pw -d mysql
  	docker run --name some-app --link some-mysql:mysql -d application-that-uses-mysql
	  docker run -it --link some-mysql:mysql --rm mysql sh -c 'exec mysql -h"$MYSQL_PORT_3306_TCP_ADDR" -P"$MYSQL_PORT_3306_TCP_PORT" -uroot -p"$MYSQL_ENV_MYSQL_ROOT_PASSWORD"'

### Nginx:  

	  docker run --name nginx --link odoo:odoo --link cms:cms -v /home/shark/nginx-config:/etc/nginx/conf.d:ro -d -p 80:80 -p 443:443 nginx
	
	
### Clojure:  

  	docker run -p 3000:3000 -d --name cms -v /home/shark/git-repo/cms:/usr/src/app -w /usr/src/app clojure lein ring server-headless
	
	
### Build, Tag, Push:  

    docker build -t xfcjscn/cms .
    docker push xfcjscn/cms





