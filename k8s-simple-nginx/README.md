## Tools

```
> kubectl version --client --short; minikube version; vboxmanage --version

Client Version: v1.19.3
minikube version: v1.14.2
commit: 2c82918e2347188e21c4e44c8056fc80408bce10
6.0.24r139119
```

## Steps

**Start the cluster and create the minikube profile**

```
> unset KUBECONFIG

> minikube -p k8s-simple-nginx start --memory=2g --cpus=4 --disk-size=2000 --kubernetes-version=v1.15.0 --vm-driver=virtualbox

😄  [k8s-simple-nginx] minikube v1.14.2 on Ubuntu 18.04
✨  Using the virtualbox driver based on user configuration
👍  Starting control plane node k8s-simple-nginx in cluster k8s-simple-nginx
🔥  Creating virtualbox VM (CPUs=4, Memory=2048MB, Disk=2000MB) ...
🐳  Preparing Kubernetes v1.15.0 on Docker 19.03.12 ...
🔎  Verifying Kubernetes components...
🌟  Enabled addons: storage-provisioner, default-storageclass

❗  /usr/local/bin/kubectl is version 1.19.3, which may have incompatibilites with Kubernetes 1.15.0.
💡  Want kubectl v1.15.0? Try 'minikube kubectl -- get pods -A'
🏄  Done! kubectl is now configured to use "k8s-simple-nginx" by default

> minikube profile k8s-simple-nginx
✅  minikube profile was successfully set to k8s-simple-nginx
```

**Check that kube-system pods are running**

```
> kubectl get pods --namespace=kube-system

NAME                                       READY   STATUS    RESTARTS   AGE
coredns-5c98db65d4-gqjmz                   1/1     Running   1          73s
etcd-k8s-simple-nginx                      1/1     Running   0          18s
kube-apiserver-k8s-simple-nginx            1/1     Running   0          12s
kube-controller-manager-k8s-simple-nginx   1/1     Running   0          4s
kube-proxy-j9bsl                           1/1     Running   0          72s
kube-scheduler-k8s-simple-nginx            1/1     Running   0          5s
storage-provisioner                        1/1     Running   1          78s
```

**Create the namespace and set it as default**

```
> kubectl create namespace k8s-simple-nginx
namespace/k8s-simple-nginx created

> kubectl config set-context $(kubectl config current-context) --namespace=k8s-simple-nginx
Context "k8s-simple-nginx" modified.
```

**Create the deployment and check that the ReplicaSet is doing its job**

```
> kubectl apply -f nginx-deployment.yaml
deployment.apps/nginx-deploy created

> kubectl get all
NAME                                READY   STATUS    RESTARTS   AGE
pod/nginx-deploy-77fff558d7-66cvq   1/1     Running   0          26s
pod/nginx-deploy-77fff558d7-jkhcr   1/1     Running   0          26s
pod/nginx-deploy-77fff558d7-nknqw   1/1     Running   0          26s

NAME                           READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/nginx-deploy   3/3     3            3           26s

NAME                                      DESIRED   CURRENT   READY   AGE
replicaset.apps/nginx-deploy-77fff558d7   3         3         3       26s

> kubectl delete pod --selector app=nginx-app #kill the pods
pod "nginx-deploy-77fff558d7-66cvq" deleted
pod "nginx-deploy-77fff558d7-jkhcr" deleted
pod "nginx-deploy-77fff558d7-nknqw" deleted

> # wait a bit...

> kubectl get all 
NAME                                READY   STATUS    RESTARTS   AGE
pod/nginx-deploy-77fff558d7-5rdnb   1/1     Running   0          21s
pod/nginx-deploy-77fff558d7-gqq98   1/1     Running   0          21s
pod/nginx-deploy-77fff558d7-v8tk7   1/1     Running   0          21s

NAME                           READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/nginx-deploy   3/3     3            3           111s

NAME                                      DESIRED   CURRENT   READY   AGE
replicaset.apps/nginx-deploy-77fff558d7   3         3         3       111s
```


**Create the service and access it from the outside**

```
> kubectl apply -f nginx-service.yaml
service/nginx-service created

> kubectl get all
NAME                                READY   STATUS    RESTARTS   AGE
pod/nginx-deploy-77fff558d7-5rdnb   1/1     Running   0          118s
pod/nginx-deploy-77fff558d7-gqq98   1/1     Running   0          118s
pod/nginx-deploy-77fff558d7-v8tk7   1/1     Running   0          118s

NAME                    TYPE       CLUSTER-IP     EXTERNAL-IP   PORT(S)        AGE
service/nginx-service   NodePort   10.102.88.92   <none>        80:30080/TCP   6s

NAME                           READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/nginx-deploy   3/3     3            3           3m28s

NAME                                      DESIRED   CURRENT   READY   AGE
replicaset.apps/nginx-deploy-77fff558d7   3         3         3       3m28s

> http $(minikube -p k8s-simple-nginx ip):30080
HTTP/1.1 200 OK
Accept-Ranges: bytes
Connection: keep-alive
Content-Length: 612
Content-Type: text/html
Date: Thu, 12 Nov 2020 07:53:08 GMT
ETag: "5f983820-264"
Last-Modified: Tue, 27 Oct 2020 15:09:20 GMT
Server: nginx/1.19.4

<!DOCTYPE html>
<html>
<head>
<title>Welcome to nginx!</title>
<style>
    body {
        width: 35em;
        margin: 0 auto;
        font-family: Tahoma, Verdana, Arial, sans-serif;
    }
</style>
</head>
<body>
<h1>Welcome to nginx!</h1>
<p>If you see this page, the nginx web server is successfully installed and
working. Further configuration is required.</p>

<p>For online documentation and support please refer to
<a href="http://nginx.org/">nginx.org</a>.<br/>
Commercial support is available at
<a href="http://nginx.com/">nginx.com</a>.</p>

<p><em>Thank you for using nginx.</em></p>
</body>
</html>
```

**Access the service from the inside**

```
> kubectl run -i --rm --restart=Never curl-client --image=tutum/curl:alpine --command -- curl -s 'http://nginx-service:80'

<!DOCTYPE html>
<html>
<head>
<title>Welcome to nginx!</title>
<style>
    body {
        width: 35em;
        margin: 0 auto;
        font-family: Tahoma, Verdana, Arial, sans-serif;
    }
</style>
</head>
<body>
<h1>Welcome to nginx!</h1>
<p>If you see this page, the nginx web server is successfully installed and
working. Further configuration is required.</p>

<p>For online documentation and support please refer to
<a href="http://nginx.org/">nginx.org</a>.<br/>
Commercial support is available at
<a href="http://nginx.com/">nginx.com</a>.</p>

<p><em>Thank you for using nginx.</em></p>
</body>
</html>
pod "curl-client" deleted

```

**Clean up and go get a cookie**

```
> minikube stop
✋  Stopping node "k8s-simple-nginx"  ...
🛑  1 nodes stopped.

> minikube delete -p k8s-simple-nginx
🔥  Deleting "k8s-simple-nginx" in virtualbox ...
💀  Removed all traces of the "k8s-simple-nginx" cluster.
```