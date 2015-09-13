#!/usr/bin/perl
# translate a mysql schema into something that can be made into a set of class defs
use strict;
my (@fields, %schema, @keys);

print "<?php\n\$schema = array();\n\n";

while (<>) {
	# this will be hidden in a comment at the top of the dump
	if (/Host:\s*(\S*)\s*Database:\s*(\S*)/) {
		print "\$dbhost = '$1';\n\$dbname = '$2';\n\n";
		next;
	}
	if (/^CREATE TABLE/.../^\).*;$/) {
		if (/^CREATE TABLE `([^`]+)`/) {
			my $table = $1;
			if (scalar(@fields)) { &printschema(); }
			print "\$schema['$table'] = array(\n";

		} elsif (/^\s*`([^`]*)` (\w+)(?:\((\d*)\)|(\(.*\))|)/) {
			my ($field,$type,$size,$opts) = ($1,$2,$3,$4);
			if ($field =~ /date|time/) {
				$size = 20;
			}
			$schema{$field} =  { type => "'$type'", size => $size };
			$schema{$field}{auto} = 1 if /AUTO_INCREMENT/i;
			if ($type =~ /enum/i) {
				$schema{$field}{opts} = "array$opts";
			}
			push @fields, $field;

		} elsif (/^\s*PRIMARY KEY\s+\((.*)\)/) {
			my $keys = $1;
			$keys =~ s/`//g;
			@keys = split ',', $keys;
			if (scalar(@keys) == 1) {
				foreach my $field (keys %schema) {
					$keys =~ s/\(\d+\)$//;
					if ($field =~ m/^($keys)$/) {
						$schema{$field}{key} = 'true';
					}
				}
			} else {
				print "\t'PRIMARY KEY' => array(";
				foreach my $key (@keys) {
					my $tb;
					$key =~ s/\(.*//;
					if ($key =~ /(\w+)_id/) {
						$tb = $1;
					}
					print "'$key' => '$tb', ";
				}
				print "),\n";
			}
		}	
	}
}

&printschema if scalar @fields;

sub printschema {
	foreach my $field (@fields) {
		print "\t'$field' => array( ";
		foreach my $prop (qw/type size rows cols key auto opts/) {
			next unless defined $schema{$field}{$prop};
			print "'$prop' => $schema{$field}{$prop}, ";
		}
		print "),\n";
	}
	print ");\n\n";
	%schema = ();
	@fields = ();
}
__END__
=head1 Name

mysql2schema.pl

=head1 Description 

purpose of this script is to read mysql CREATE TABLE statements and 
make a schema file from them 

=head1 Synopsis

 mysql2schema.pl mysql-dump-file > my-schema-file.php
 
or 

 mysqldump ... | mysql2schema.pl > my-schema-file.php

you should then edit the my-schema-file.php file as needed before making 
Entity and Relation child classes based on each table

=head1 TODO

make a tool to do this automatically

=head1 Notes

if you use the form {tablename}_id for the primary key for Entity tables
(ie where there is only one key) this will correctly add the table
but this can be added by hand later

=head1 Copyright

 Author Cal Woodruff cwoodruf@gmail.com
 Licensed under the Perl Artistic License version 2.0
 http://www.perlfoundation.org/attachment/legal/artistic-2_0.txt

=cut
