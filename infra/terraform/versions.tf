terraform {
  required_version = ">= 0.13"

  required_providers {
    digitalocean = {
      source  = "digitalocean/digitalocean"
      version = "2.22.2"
    }
  }
}
