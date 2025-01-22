terraform {
  required_version = ">= 1.10"

  required_providers {
    digitalocean = {
      source  = "digitalocean/digitalocean"
      version = "2.47.0"
    }
    uptimerobot = {
      source  = "vexxhost/uptimerobot"
      version = "0.8.2"
    }
    mailgun = {
      source  = "wgebis/mailgun"
      version = "0.7.7"
    }
  }
}
