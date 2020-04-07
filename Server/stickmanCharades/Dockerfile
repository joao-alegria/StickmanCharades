FROM payara/server-full:5.194

ADD --chown=payara:payara https://jdbc.postgresql.org/download/postgresql-42.2.11.jar /opt/payara/appserver/glassfish/domains/production/lib

COPY domain.xml /opt/payara/appserver/glassfish/domains/production/config

COPY target/stickman-charades.war /opt/payara/deployments
