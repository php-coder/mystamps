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
  ```
* Plan and apply:
  ```console
  $ terraform plan -out terraform.tfplan
  $ terraform apply terraform.tfplan
  ```
