import { Eureka } from 'eureka-js-client';

export function registerEureka(appName, port) {
  const client = new Eureka({
    // application instance information
    instance: {
      app: appName,
      hostName: 'localhost',
      ipAddr: '127.0.0.1',
      port: {
          '$': port,
          '@enabled': true,
        },
      vipAddress: appName,
      dataCenterInfo: {
          '@class': 'com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo',
          name: 'MyOwn',
      },
    },
    eureka: {
      host: '127.0.0.1',
      port: 8761,
      servicePath: '/eureka/apps/'
    },
  });

  client.start();
}