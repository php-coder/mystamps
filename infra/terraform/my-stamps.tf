# @todo #1000 CI: validate and check Terraform configuration
# @todo #1000 Terraform: add Mailgun

# How to obtain a token: https://docs.digitalocean.com/reference/api/create-personal-access-token/
variable "do_token" {
  description = "Digital Ocean Personal Access Token"
  type        = string
  sensitive   = true
}

# How to create API Key:
# - open https://uptimerobot.com/dashboard
# - go to MySettings
# - scroll down to API Settings and select "Main API Key"
variable "uptimerobot_token" {
  description = "UptimeRobot API key"
  type        = string
  sensitive   = true
}

# Digital Ocean provider docs: https://registry.terraform.io/providers/digitalocean/digitalocean/2.28.1/docs
provider "digitalocean" {
  token = var.do_token
}

# Droplet docs: https://registry.terraform.io/providers/digitalocean/digitalocean/2.28.1/docs/resources/droplet.html
resource "digitalocean_droplet" "web" {
  image  = "ubuntu-24-04-x64"
  name   = "my-stamps.ru"
  region = "fra1"
  size   = "s-1vcpu-1gb"
  # https://developer.hashicorp.com/terraform/language/meta-arguments/lifecycle
  lifecycle {
    ignore_changes = [image]
  }
}

# Domain docs: https://registry.terraform.io/providers/digitalocean/digitalocean/2.28.1/docs/resources/domain.html
resource "digitalocean_domain" "site" {
  name = "my-stamps.ru"
}

# Record docs: https://registry.terraform.io/providers/digitalocean/digitalocean/2.28.1/docs/resources/record.html
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

resource "digitalocean_record" "email" {
  domain = digitalocean_domain.site.name
  type   = "CNAME"
  name   = "email"
  value  = "mailgun.org."
}

resource "digitalocean_record" "verification" {
  domain = digitalocean_domain.site.name
  type   = "TXT"
  name   = "@" # <-- to match the current settings. It's better to use "verification" instead
  value  = "globalsign-domain-verification=405tmKGIyZt12MvKu_nJV1oCJ_e-MEjf_26bcFQX0t"
}

resource "digitalocean_record" "spf" {
  domain = digitalocean_domain.site.name
  type   = "TXT"
  name   = "@" # <-- to match the current settings. It's better to use "spf" instead
  value  = "v=spf1 include:mailgun.org ~all"
}

resource "digitalocean_record" "domain_key" {
  domain = digitalocean_domain.site.name
  type   = "TXT"
  name   = "mx._domainkey"
  value  = "k=rsa; p=MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDLo/TZgHYIM7xrU9dBJCJTTsP90rGhHuyTqSrC3LT+T3vsH5azrz1+9Dm86xz6TpcmrHV1WgmSnw72C++AXstlS8CEg6Z6XVuxMDKsMnMVEWDm1bpESy+h29Ns3kY/EzMTaF1V88ICmr6fSpQIOd9u/lZpsABjfh2wfag1rqWcGwIDAQAB"
}


# UptimeRobot provider docs: https://registry.terraform.io/providers/vexxhost/uptimerobot/0.8.2/docs
provider "uptimerobot" {
  api_key = var.uptimerobot_token
}

# https://registry.terraform.io/providers/vexxhost/uptimerobot/0.8.2/docs/resources/alert_contact
resource "uptimerobot_alert_contact" "email" {
  value         = "slava.semushin@gmail.com"
  type          = "e-mail"
  friendly_name = "slava.semushin@gmail.com"
}

# https://registry.terraform.io/providers/vexxhost/uptimerobot/0.8.2/docs/resources/monitor
resource "uptimerobot_monitor" "mystamps" {
  url           = "https://my-stamps.ru"
  type          = "http"
  friendly_name = "MyStamps"
  interval      = 300 # 300 seconds by default

  alert_contact {
    id = uptimerobot_alert_contact.email.id
  }
}

# https://registry.terraform.io/providers/vexxhost/uptimerobot/0.8.2/docs/resources/status_page
resource "uptimerobot_status_page" "status_page" {
  friendly_name = "MyStamps Site Status"
  monitors      = [uptimerobot_monitor.mystamps.id]
}
