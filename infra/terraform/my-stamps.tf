# @todo #1000 CI: validate and check Terraform configuration

variable "token" {}

# Provider docs: https://www.terraform.io/docs/providers/do/index.html
provider "digitalocean" {
  token   = var.token
  version = "~> 1.12"
}

# Droplet docs: https://www.terraform.io/docs/providers/do/r/droplet.html
resource "digitalocean_droplet" "web" {
  # "ubuntu-16-04-x64" resolves into Ubuntu 16.04.6 while our server is based on Ubuntu 16.04.1
  image              = "18572320"
  name               = "my-stamps.ru"
  region             = "fra1"
  size               = "s-1vcpu-1gb"
  private_networking = true
}

# https://www.terraform.io/docs/providers/do/r/domain.html
resource "digitalocean_domain" "site" {
  name = "my-stamps.ru"
}
