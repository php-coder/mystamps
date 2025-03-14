# Provisioning a server with Terraform

<!--
@todo #1720 Terraform: automate import of the existing resources
-->

* Install Terraform
  * the preferred way is to use [`mise`](https://mise.jdx.dev/getting-started.html). After its activation,
    Terraform will be installed automatically once you `cd` into the `infra/terraform` directory
* Navigate to the directory with Terraform configuration
  ```console
  $ cd infra/terraform
  ```
* Setup credentials (only first time)
  ```console
  $ cp terraform.tfvars{.example,}
  $ vim terraform.tfvars
  ```
* Initialize and download modules (if needed)
  ```console
  $ terraform init
  ```
* Import existing DigitalOcean configuration (optionally; only the first time)
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
* Import existing UptimeRobot configuration (optionally; only the first time)
  ```console
  $ terraform import uptimerobot_alert_contact.email <id>
  $ terraform import uptimerobot_monitor.mystamps <id>
  $ terraform import uptimerobot_status_page.status_page <id>
  ```
  The ids can be obtained by making `/v2/getAlertContacts`, `/v2/getMonitors`, and `/v2/getPSPs` API calls (see https://uptimerobot.com/api/ for details).
  For example:
  ```console
  $ export UPTIMEROBOT_TOKEN="$(grep -Po 'uptimerobot_token = "\K[^\"]+' terraform.tfvars)"
  $ curl -sS -H 'Content-Type: application/x-www-form-urlencoded' -H 'Cache-Control: no-cache' -d "api_key=$UPTIMEROBOT_TOKEN" 'https://api.uptimerobot.com/v2/getAlertContacts' | jq -r '.alert_contacts[].id'
  1234567
  $ curl -sS -H 'Content-Type: application/x-www-form-urlencoded' -H 'Cache-Control: no-cache' -d "api_key=$UPTIMEROBOT_TOKEN" 'https://api.uptimerobot.com/v2/getMonitors' | jq '.monitors[].id'
  123456789
  $ curl -sS -H 'Content-Type: application/x-www-form-urlencoded' -H 'Cache-Control: no-cache' -d "api_key=$UPTIMEROBOT_TOKEN" 'https://api.uptimerobot.com/v2/getPSPs' | jq '.psps[].id'
  1234
  ```
* Import existing MailGun configuration (optionally; only the first time)
  ```console
  $ terraform import mailgun_domain.mystamps us:my-stamps.ru
  ```
* Plan and apply:
  ```console
  $ terraform plan -out terraform.tfplan
  $ terraform apply terraform.tfplan
  ```
