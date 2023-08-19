#!/bin/bash
BASEPATH=$(cd "$(dirname "$0")";pwd)
VERSION="v1.0.0"

REGNAME="registry.cn-beijing.aliyuncs.com/kischang/self"
#REGNAME="registry.us-west-1.aliyuncs.com/kischang/selfus"

cd ${BASEPATH}/ || exit
git pull origin master

# build web
if [[ $1 == 'w' || $1 == 'all' ]];then
  cd ${BASEPATH}/kindle || exit
  yarn && yarn run build
  cd ${BASEPATH}/web || exit
  yarn && yarn run build
fi

# build root api
if [[ $1 == 'b' || $1 == 'all' ]];then
  cd ${BASEPATH}/ || exit
  mvn clean package -Dmaven.test.skip=true
fi

#  --progress plain 加上可以看ls输出
if [[ $2 == 'us' ]];then
  REGNAME="registry.us-west-1.aliyuncs.com/kischang/selfus"
fi
docker build -f ./docker/root.Dockerfile -t ${REGNAME}:root-${VERSION} .
docker push ${REGNAME}:root-${VERSION}