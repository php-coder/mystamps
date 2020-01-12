# Provisioning a server with Terraform

* Setup credentials (only first time)
  ```console
  $ cd infra/terraform
  $ cp terraform.tfvars{.example,}
  $ vim terraform.tfvars
  ```
* Initialize and download modules (if needed)
  ```console
  $ terraform init
  ```
* Import existing configuration (optionally; only first time)
  ```console
  $ terraform import digitalocean_droplet.web <id>
  $ terraform import digitalocean_domain.site my-stamps.ru
  $ terraform import digitalocean_record.www my-stamps.ru,<id>
  $ terraform import digitalocean_record.no-www my-stamps.ru,<id>
  $ terraform import digitalocean_record.ns1 my-stamps.ru,<id>
  $ terraform import digitalocean_record.ns2 my-stamps.ru,<id>
  $ terraform import digitalocean_record.ns3 my-stamps.ru,<id>
  $ terraform import digitalocean_record.mx1 my-stamps.ru,<id>
  $ terraform import digitalocean_record.mx2 my-stamps.ru,<id>
  $ terraform import digitalocean_record.email my-stamps.ru,<id>
  ```
  The ids can be obtained by API:
  - https://developers.digitalocean.com/documentation/v2/#list-all-droplets
  - https://developers.digitalocean.com/documentation/v2/#list-all-domain-records
* Plan and apply:
  ```console
  $ terraform plan -out terraform.tfplan
  $ terraform apply terraform.tfplan
  ```
