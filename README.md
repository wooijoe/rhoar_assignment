# rhoar_assignment
Assess hands-on proficiency with Red Hat OpenShift Application Runtimes (RHOAR)

# Log in OpenShift
$ oc login --insecure-skip-tls-verify --server=https://master.na39.openshift.opentlc.com:443

An existing project is already created called "freelancer-project"

# Sample curl Requests
$ export projectName = freelancer-project
$ export PROJECT_URL=http://$(oc get route project-service -n $projectName -o template --template='{{.spec.host}}')
$ curl -X GET "$PROJECT_URL/projects"
$ curl -X GET "$PROJECT_URL/projects/proj001"
$ curl -X GET "$PROJECT_URL/projects/status/open"

$ export FREELANCER_URL=http://$(oc get route freelancer-service -n $projectName -o template --template='{{.spec.host}}')
$ curl -X GET "$FREELANCER_URL/freelancers"
$ curl -X GET "$FREELANCER_URL/freelancers/fl01"

$ export GATEWAY_URL=http://$(oc get route gateway-service -n $projectName -o template --template='{{.spec.host}}')
$ curl -X GET "$GATEWAY_URL/gateway/projects"
$ curl -X GET "$GATEWAY_URL/gateway/projects/proj001"
$ curl -X GET "$GATEWAY_URL/gateway/projects/status/open"
$ curl -X GET "$GATEWAY_URL/gateway/freelancers"
$ curl -X GET "$GATEWAY_URL/gateway/freelancers/fl01"
