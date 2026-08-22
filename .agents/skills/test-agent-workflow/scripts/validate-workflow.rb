#!/usr/bin/env ruby
# frozen_string_literal: true

require "yaml"

workflow = File.expand_path("..", __dir__)
skills_root = File.expand_path("..", workflow)
repo = File.expand_path("../../..", workflow)
registry_path = File.join(workflow, "references/workflow-registry.yml")
errors = []

registry = YAML.load_file(registry_path)
public_skills = registry.fetch("public_skills")
expected_names = %w[
  test-agent-workflow
  test-case-design
  test-case-quality-review
  test-strategy
  test-execution
  test-agent-governance
]

errors << "public Skill set differs from target" unless public_skills.keys.sort == expected_names.sort

public_skills.each do |name, entry|
  path = File.join(repo, entry.fetch("path"))
  unless File.file?(path)
    errors << "missing public Skill: #{entry['path']}"
    next
  end

  text = File.read(path)
  errors << "Skill name mismatch for #{name}" unless text.match?(/\A---\s*\n.*?^name:\s*#{Regexp.escape(name)}\s*$.*?^description:\s*\S/m)
end

actual_skill_files = Dir[File.join(skills_root, "**/SKILL.md")].sort
errors << "expected 6 public SKILL.md files, found #{actual_skill_files.size}" unless actual_skill_files.size == 6

registry.fetch("internal_stages").each_value do |relative|
  errors << "missing internal stage: #{relative}" unless File.file?(File.join(workflow, relative))
end

required_references = %w[
  test-agent-workflow/references/artifact-contracts.md
  test-agent-workflow/references/final-test-report.md
  test-case-design/references/existing-case-discovery.md
  test-case-design/references/case-design-schema.md
  test-case-design/references/test-design-techniques.md
  test-case-design/references/cause-effect-graph.md
  test-case-quality-review/references/review-rubric.md
  test-strategy/references/layer-capabilities.md
  test-strategy/references/test-pyramid.md
  test-strategy/references/execution-plan-schema.md
  test-execution/references/evidence-schema.md
  test-execution/references/runtimes/colima.md
  test-agent-governance/references/quality-kb-builder.md
  test-agent-governance/references/skill-benchmark.md
  test-agent-governance/references/skill-self-optimization.md
  test-agent-governance/references/ci-quality-flow.md
]
required_references.each do |relative|
  errors << "missing reference: #{relative}" unless File.file?(File.join(skills_root, relative))
end

layer_references = Dir[File.join(skills_root, "test-execution/references/layers/*.md")]
errors << "expected 6 execution layer references, found #{layer_references.size}" unless layer_references.size == 6

design = File.read(File.join(skills_root, "test-case-design/SKILL.md"))
review = File.read(File.join(skills_root, "test-case-quality-review/SKILL.md"))
strategy = File.read(File.join(skills_root, "test-strategy/SKILL.md")) +
  File.read(File.join(skills_root, "test-strategy/references/execution-plan-schema.md"))
execution = File.read(File.join(skills_root, "test-execution/SKILL.md")) +
  File.read(File.join(skills_root, "test-execution/references/evidence-schema.md"))

%w[layer_reference command_source execution_profile].each do |term|
  errors << "Test Case Design owns Strategy field: #{term}" if design.include?(term)
end
%w[layer_reference command_source].each do |term|
  errors << "Quality Review owns Strategy field: #{term}" if review.include?(term)
end
%w[test_case_id execution_id layer_reference command_source runtime expected_evidence].each do |term|
  errors << "Test Strategy misses execution field: #{term}" unless strategy.include?(term)
end
%w[test_case_id execution_id PASSED FAILED BLOCKED NOT_PROVEN INCOMPLETE].each do |term|
  errors << "Test Execution misses evidence term: #{term}" unless execution.include?(term)
end

governance_policy = YAML.load_file(File.join(skills_root, "test-agent-governance/agents/openai.yaml"))
unless governance_policy.dig("policy", "allow_implicit_invocation") == false
  errors << "test-agent-governance must be explicit-only"
end

custom_agents = registry.fetch("custom_agents")
test_engineer_path = File.join(repo, custom_agents.fetch("test_engineer").fetch("path"))
unless File.file?(test_engineer_path)
  errors << "missing project custom agent: .codex/agents/test-engineer.toml"
else
  agent = File.read(test_engineer_path)
  %w[name description developer_instructions].each do |field|
    errors << "test_engineer misses #{field}" unless agent.match?(/^#{field}\s*=/)
  end
end

legacy_persona_dir = File.join(workflow, "references", "personas")
errors << "legacy persona reference directory still exists" if Dir.exist?(legacy_persona_dir)

Dir[File.join(skills_root, "**/*.{md,yml,yaml}")].each do |file|
  text = File.read(file)
  %w[layer_skill test-agent-orchestrator].each do |term|
    errors << "stale term #{term.inspect} in #{file.delete_prefix(repo + '/')}" if text.include?(term)
  end
  errors << "project name leaked into #{file.delete_prefix(repo + '/')}" if text.match?(/lite.?mall/i)
  errors << "Test Harness dependency leaked into #{file.delete_prefix(repo + '/')}" if text.match?(/test.?harness/i)

  text.scan(/\[[^\]]+\]\(([^)]+)\)/).flatten.each do |link|
    next if link.match?(%r{\A(?:https?://|#)})
    target = link.split("#", 2).first
    next if target.empty?
    errors << "broken local link #{link} in #{file.delete_prefix(repo + '/')}" unless File.exist?(File.expand_path(target, File.dirname(file)))
  end
end

compatibility = File.join(repo, "skills")
unless File.symlink?(compatibility) && File.realpath(compatibility) == File.realpath(skills_root)
  errors << "skills compatibility link must resolve to .agents/skills"
end

legacy_root = File.join(repo, "AI Native/skills/test-agent-workflow")
errors << "legacy workflow implementation still exists" if File.exist?(legacy_root)

if errors.empty?
  puts "workflow validation passed"
  puts "public_skills=#{actual_skill_files.size}"
  puts "execution_layer_references=#{layer_references.size}"
  exit 0
end

warn errors.uniq.join("\n")
exit 1
