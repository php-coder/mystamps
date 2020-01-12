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

# Domain docs: https://www.terraform.io/docs/providers/do/r/domain.html
resource "digitalocean_domain" "site" {
  name = "my-stamps.ru"
}

# Record docs: https://www.terraform.io/docs/providers/do/r/record.html
resource "digitalocean_record" "no-www" {
  domain = digitalocean_domain.site.name
  type   = "A"
  name   = "@"
  value  = digitalocean_droplet.web.ipv4_address
}
resource "digitalocean_record" "www" {
  domain = digitalocean_domain.site.name
  type   = "A"
  name   = "www"
  value  = digitalocean_droplet.web.ipv4_address
}
resource "digitalocean_record" "ns1" {
  domain = digitalocean_domain.site.name
  type   = "NS"
  name   = "@" # <-- to match the current settings. It's better to use "ns1" instead
  value  = "ns1.digitalocean.com."
}
resource "digitalocean_record" "ns2" {
  domain = digitalocean_domain.site.name
  type   = "NS"
  name   = "@" # <-- to match the current settings. It's better to use "ns2" instead
  value  = "ns2.digitalocean.com."
}
resource "digitalocean_record" "ns3" {
  domain = digitalocean_domain.site.name
  type   = "NS"
  name   = "@" # <-- to match the current settings. It's better to use "ns3" instead
  value  = "ns3.digitalocean.com."
}
resource "digitalocean_record" "mx1" {
  domain   = digitalocean_domain.site.name
  type     = "MX"
  name     = "@" # <-- to match the current settings. It's better to use "mx1" instead
  value    = "mxa.mailgun.org."
  priority = 10
}
resource "digitalocean_record" "mx2" {
  domain   = digitalocean_domain.site.name
  type     = "MX"
  name     = "@" # <-- to match the current settings. It's better to use "mx2" instead
  value    = "mxb.mailgun.org."
  priority = 10
}
