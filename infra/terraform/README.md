# Provisioning a server with Terraform

* Install [tfenv](https://github.com/tfutils/tfenv) and terraform
  ```console
  $ brew install tfenv
  $ cd infra/terraform
  $ export HTTPS_PROXY=socks5://127.0.0.1:1122 # optionally
  $ tfenv install
  ```
* Setup credentials (only first time)
  ```console
  $ cp terraform.tfvars{.example,}
  $ vim terraform.tfvars
  ```
* Initialize and download modules (if needed)
  ```console
  $ export HTTPS_PROXY=socks5://127.0.0.1:1122 # optionally
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
  $ terraform import digitalocean_record.spf my-stamps.ru,<id>
  $ terraform import digitalocean_record.verification my-stamps.ru,<id>
  $ terraform import digitalocean_record.domain_key my-stamps.ru,<id>
  ```
  The ids can be obtained by API:
  - https://docs.digitalocean.com/reference/api/api-reference/#operation/droplets_list
  - https://docs.digitalocean.com/reference/api/api-reference/#operation/domains_list_records
  For example:
  ```console
  $ export DIGITALOCEAN_TOKEN="$(grep -Po 'do_token = "\K[^\"]+' terraform.tfvars)"
  $ curl -sS -H "Content-Type: application/json" -H "Authorization: Bearer $DIGITALOCEAN_TOKEN" "https://api.digitalocean.com/v2/droplets" | jq '.droplets[].id'
  12345678
  $ curl -sS -H "Content-Type: application/json" -H "Authorization: Bearer $DIGITALOCEAN_TOKEN" "https://api.digitalocean.com/v2/domains/my-stamps.ru/records" | jq
  ```
* Plan and apply:
  ```console
  $ terraform plan -out terraform.tfplan
  $ terraform apply terraform.tfplan
  ```
