# 使用openjdk8的镜像
FROM openjdk:8-jdk-alpine
COPY --from=hengyunabc/arthas:latest /opt/arthas /opt/arthas

#指定镜像创建者信息
MAINTAINER com.zetyun
RUN echo -e http://mirrors.ustc.edu.cn/alpine/v3.7/main/ > /etc/apk/repositories
RUN apk add --no-cache  bash bash-doc bash-completion

# 设置工作目录
WORKDIR /opt/statics

# Add Tini
ADD tini.sh /opt/statics/tini.sh
RUN chmod +x /opt/statics/tini.sh

# Copy Config
COPY config.properties /opt/statics/


# 暴露指定的端口
ARG JAR_FILE
EXPOSE 18000
# 执行发布命令
ADD  ${JAR_FILE} /opt/statics/app.jar
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories && apk add --update ttf-dejavu fontconfig && rm -rf /var/cache/apk/* && mkfontscale && mkfontdir && fc-cache
RUN apk add --no-cache  bash bash-doc bash-completion

ENTRYPOINT ["/bin/bash", "-c", "/opt/bap/tini.sh"]