FROM fabric8/s2i-java:2.0
ENV JAVA_OPTIONS=-Dvertx.cacheDirBase=/tmp JAVA_APP_DIR=/deployments
EXPOSE 8080 8778 9779
COPY maven /deployments/
