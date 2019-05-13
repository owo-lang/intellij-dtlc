use strict;
use warnings FATAL => 'all';

foreach my $svg (split /[ \t\n]+/, `ls *.svg`) {
	my $png = $svg =~ s/svg/png/rg;
	`"C:\\Program Files\\Inkscape\\inkscape.exe" $svg --export-png=$png`;
}
