FROM openjdk:17-oracle
RUN mkdir -p /opt/apps/coinbaseorderbook
COPY coinbaseorderbook-1.0.jar /opt/apps/coinbaseorderbook
ENTRYPOINT ["java", "-jar", "/opt/apps/coinbaseorderbook/coinbaseorderbook-1.0.jar", "BTC-USD"]