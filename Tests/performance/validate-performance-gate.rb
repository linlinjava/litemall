#!/usr/bin/env ruby
# frozen_string_literal: true

require "yaml"

path = File.expand_path("performance-gate.yml", __dir__)
gate = YAML.load_file(path)

required_top = %w[
  version project quality_dimension status source_design metric_honesty_rule
  workflow frontend_targets backend_targets audit_targets ci_gates
]

missing = required_top.reject { |key| gate.key?(key) }
abort "Missing top-level fields: #{missing.join(', ')}" unless missing.empty?

expected_workflow = %w[measure identify fix verify guard]
unless gate.fetch("workflow") == expected_workflow
  abort "workflow must be #{expected_workflow.join(' -> ')}"
end

honesty = gate.fetch("metric_honesty_rule")
%w[quick_mode deep_mode forbidden].each do |key|
  abort "metric_honesty_rule.#{key} is required" unless honesty[key]
end

frontend_targets = gate.fetch("frontend_targets")
%w[lighthouse_performance lcp_ms inp_ms cls initial_js_gzip_kb].each do |key|
  target = frontend_targets[key] || abort("frontend target #{key} is required")
  abort "frontend target #{key} must define target" unless target.key?("target")
  abort "frontend target #{key} must define source" unless target["source"]
end

backend_targets = gate.fetch("backend_targets")
%w[order_detail_api_p95_ms submit_order_api_p95_ms pay_notify_api_p95_ms].each do |key|
  target = backend_targets[key] || abort("backend target #{key} is required")
  abort "backend target #{key} must define target" unless target.key?("target")
  abort "backend target #{key} must define source" unless target["source"]
end

ci_gates = gate.fetch("ci_gates")
%w[pr_perf nightly_perf release_perf].each do |key|
  lane = ci_gates[key] || abort("ci gate #{key} is required")
  abort "ci gate #{key} must define mode" unless lane["mode"]
  abort "ci gate #{key} must define required_artifacts" unless lane["required_artifacts"].is_a?(Array)
end

puts "[performance] performance-gate.yml is valid"
puts "[performance] Current lane validates gate design only; measured Lighthouse/CrUX/API artifacts are required for Deep mode."
