<!--- some badges to display on the GitHub page -->

![Travis (.org)](https://img.shields.io/travis/debuglevel/street-lister?label=Travis%20build)
![Gitlab pipeline status](https://img.shields.io/gitlab/pipeline/debuglevel/street-lister?label=GitLab%20build)
![GitHub release (latest SemVer)](https://img.shields.io/github/v/release/debuglevel/street-lister?sort=semver)
![GitHub](https://img.shields.io/github/license/debuglevel/street-lister)
[![Gitpod ready-to-code](https://img.shields.io/badge/Gitpod-ready--to--code-blue?logo=gitpod)](https://gitpod.io/#https://github.com/debuglevel/street-lister)

# Street lister
This microservice extracts and keeps updated streets and their postal codes from OpenStreetMaps via Overpass API.

To do so, it first queries for all postal codes in an area (most useful a country) and then gets the streets for each postal code area. 

# HTTP API
TODO
## OpenAPI / Swagger

There is an OpenAPI (former: Swagger) specification created, which is available at <http://localhost:8080/swagger/greeter-microservice-0.1.yml>, `build/tmp/kapt3/classes/main/META-INF/swagger/` in the source directory and `META-INF/swagger/` in the jar file. It can easily be pasted into the [Swagger Editor](https://editor.swagger.io) which provides a live demo for [Swagger UI](https://swagger.io/tools/swagger-ui/), but also offers to create client libraries via [OpenAPI Generator](https://openapi-generator.tech).

## Add person
```bash
$ curl --location --request POST 'http://localhost:8080/persons/' \
--header 'Content-Type: application/json' \
--data-raw '{
    "name": "Foo Bar"
}'

{
    "id": "3e266d3b-df74-4918-9d5d-22a5983e9dc2",
    "name": "Foo Bar"
}
```

## Get person
```bash
$ curl --location --request GET 'http://localhost:8080/persons/3e266d3b-df74-4918-9d5d-22a5983e9dc2'

{
    "id": "3e266d3b-df74-4918-9d5d-22a5983e9dc2",
    "name": "Foo Bar"
}
```

# Configuration

There is a `application.yml` included in the jar file. Its content can be modified and saved as a separate `application.yml` on the level of the jar file. Configuration can also be applied via the other supported ways of Micronaut (see <https://docs.micronaut.io/latest/guide/index.html#config>). For Docker, the configuration via environment variables is the most interesting one (see `docker-compose.yml`).