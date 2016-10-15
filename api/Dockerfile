FROM node

RUN apt-get update && apt-get install libstdc++6
VOLUME ["/src"]
ADD start.sh /start.sh
RUN chmod 755 /start.sh
ENV NODE_ENV dev
CMD ["/start.sh"]
EXPOSE 3000
