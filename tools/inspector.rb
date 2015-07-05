# Reports information about FileSize and File Locations
require 'gist'
require 'json'
begin
  job_num = ENV["TRAVIS_JOB_NUMBER"]
  if ENV["TRAVIS_PULL_REQUEST"] == "false"
    exported_libs = Dir["build/libs/Toast-*.jar"]
    @data = { :files => {} }
    exported_libs.each do |file_name|
      if file_name =~ /.*Toast-(.*)-raw.jar/
        @data[:files][:raw] = file_name
      elsif file_name =~ /.*Toast-(.*)-sources.jar/
        @data[:files][:sources] = file_name
      elsif file_name =~ /.*Toast-(.*).jar/
        @data[:files][:jar] = file_name
      end
    end
    @data[:sizes] = @data[:files].map { |type, name| {type => File.size(name)} }.reduce(:merge)
    puts "Metrics uploaded to Gist: #{Gist.gist(File.read("tools/stats/metrics.json"), {:access_token => ENV['GIST_URL'], :filename => "metrics_#{job_num}.json", :public => true})["html_url"]}"
    puts "Inspector Complete: #{Gist.gist("#{JSON.generate(@data)}", {:access_token => ENV['GIST_URL'], :filename => "inspector_#{job_num}.json", :public => true})["html_url"]}"
  else
    puts "Pull Request -- Metrics Disabled"
  end
rescue => e
  puts "Could not Upload Metrics :c"
end
